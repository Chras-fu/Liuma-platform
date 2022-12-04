package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.DeviceStatus;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.common.constants.TaskType;
import com.autotest.LiuMa.common.utils.HttpUtils;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.StatisticsDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
public class ScheduleJobService {

    @Resource
    private EngineMapper engineMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private PlanMapper planMapper;

    @Resource
    private PlanScheduleMapper planScheduleMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private StatisticsMapper statisticsMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private DeviceMapper deviceMapper;

    public void updateLostHeartbeatEngine(){
        Long minLastHeartbeatTime = System.currentTimeMillis() - 3*60*1000; // 三分钟没有心跳监控则离线
        engineMapper.updateLostHeartbeatEngine(minLastHeartbeatTime);
    }

    public void updateTimeoutTask(){
        Long minLastUploadTime = System.currentTimeMillis() - 30*60*1000;   // 半小时内没有结果返回则任务超时
        Long minLastToRunTime = System.currentTimeMillis() - 2*60*60*1000;   // 两小时内没有执行则任务超时
        List<Report> reports = reportMapper.selectTimeoutReport(minLastUploadTime, minLastToRunTime);
        for(Report report:reports){
            reportMapper.updateReportStatus(ReportStatus.DISCONTINUE.toString(), report.getId());
            taskMapper.updateTask(ReportStatus.DISCONTINUE.toString(), report.getTaskId());
            reportMapper.updateReportEndTime(report.getId(), System.currentTimeMillis(), System.currentTimeMillis());
        }
    }

    public void updateTimeoutDevice(){
        List<Device> devices = deviceMapper.selectTimeoutDevice();
        for (Device device:devices){
            JSONObject sources = JSONObject.parseObject(device.getSources());
            device.setStatus(DeviceStatus.COLDING.toString());
            device.setUpdateTime(System.currentTimeMillis());
            device.setSources("{}");
            device.setUser("");
            device.setTimeout(0);
            // 调用设备冷却接口
            String url = sources.getString("url");
            HttpUtils.post(url+"/cold?udid="+device.getSerial(), null);
            deviceMapper.updateDevice(device);
        }
    }

    public void runSchedulePlan(){
        long currentTime = System.currentTimeMillis();
        Long minNextRunTime = currentTime - 30*1000;
        Long maxNextRunTime = currentTime + 30*1000;   // 查找下次执行时间在前后半分钟内的计划
        List<PlanSchedule> planSchedules = planScheduleMapper.getToRunPlanScheduleList(minNextRunTime, maxNextRunTime);
        for(PlanSchedule planSchedule: planSchedules){
            Plan plan = planMapper.getPlanDetail(planSchedule.getPlanId());
            String runName = plan.getName() +"-"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Task task = new Task();
            task.setId(UUID.randomUUID().toString());
            task.setName(runName);
            task.setStatus(ReportStatus.PREPARED.toString());
            task.setType(TaskType.SCHEDULE.toString());
            task.setEngineId(plan.getEngineId());
            task.setProjectId(plan.getProjectId());
            task.setCreateUser(plan.getCreateUser());
            task.setUpdateUser(plan.getCreateUser());
            task.setCreateTime(System.currentTimeMillis());
            task.setUpdateTime(System.currentTimeMillis());
            taskMapper.addTask(task);
            // 预设报告
            Report report = new Report();
            report.setId(UUID.randomUUID().toString());
            report.setName(runName);
            report.setTaskId(task.getId());
            report.setEnvironmentId(plan.getEnvironmentId());
            report.setDeviceId(null);
            report.setSourceType(ReportSourceType.PLAN.toString());
            report.setSourceId(plan.getId());
            report.setStatus(ReportStatus.PREPARED.toString());
            report.setProjectId(plan.getProjectId());
            report.setCreateUser(plan.getCreateUser());
            report.setUpdateUser(plan.getCreateUser());
            report.setCreateTime(System.currentTimeMillis());
            report.setUpdateTime(System.currentTimeMillis());
            reportMapper.addReport(report);
            // 统计报告用例数
            ReportStatistics reportStatistics = new ReportStatistics();
            reportStatistics.setId(UUID.randomUUID().toString());
            reportStatistics.setReportId(report.getId());
            reportStatistics.setPassCount(0);
            reportStatistics.setErrorCount(0);
            reportStatistics.setFailCount(0);
            Integer total = planCollectionMapper.getPlanCaseCount(plan.getId());
            reportStatistics.setTotal(total);
            reportMapper.addReportStatistics(reportStatistics);
            // 回写定时任务表下次执行时间
            planSchedule.setNextRunTime(PlanService.getNextRunTime(planSchedule.getNextRunTime(), planSchedule.getFrequency()));
            planScheduleMapper.updatePlanSchedule(planSchedule);
        }
    }

    public void statisticsData(){
        // 所有项目
        List<String> projectIds = projectMapper.getAllProjectId();
        // 当前日期
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);
        // 总数统计
        HashMap<String, SumStatistics> sumStatisticsMap = new HashMap<>();
        // 每日统计
        HashMap<String, DailyStatistics> dailyStatisticsMap = new HashMap<>();

        for(String projectId: projectIds){
            SumStatistics sum = new SumStatistics();
            sum.setId(UUID.randomUUID().toString());
            sum.setProjectId(projectId);
            JSONObject prObj = new JSONObject();
            prObj.put("xAxis", new JSONArray());
            prObj.put("planRunTotal", new JSONArray());
            prObj.put("planRunPass", new JSONArray());
            prObj.put("planRunPassRate", new JSONArray());
            prObj.put("yMaxLeft", 0);
            sum.setPlanRunWeekTop(prObj.toString());
            JSONObject cfObj = new JSONObject();
            cfObj.put("x", new JSONArray());
            cfObj.put("y", new JSONArray());
            sum.setCaseFailWeekTop(cfObj.toString());
            sumStatisticsMap.put(projectId, sum);
            DailyStatistics daily = new DailyStatistics();
            daily.setId(UUID.randomUUID().toString());
            daily.setProjectId(projectId);
            daily.setStatDate(currentDate);
            dailyStatisticsMap.put(projectId, daily);
        }

        List<StatisticsDTO> caseTotal = statisticsMapper.getCaseCountByProject();
        for(StatisticsDTO statisticsDTO: caseTotal){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            DailyStatistics daily = dailyStatisticsMap.get(statisticsDTO.getProjectId());
            if(statisticsDTO.getName().equals("API")){
                sum.setApiCaseTotal(statisticsDTO.getCount());
                daily.setApiCaseSum(statisticsDTO.getCount());
            }else if(statisticsDTO.getName().equals("WEB")){
                sum.setWebCaseTotal(statisticsDTO.getCount());
                daily.setWebCaseSum(statisticsDTO.getCount());
            }else {
                sum.setAppCaseTotal(statisticsDTO.getCount());
                daily.setAppCaseSum(statisticsDTO.getCount());
            }
        }
        List<StatisticsDTO> caseNewToday = statisticsMapper.getCaseTodayNewCountByProject();
        for(StatisticsDTO statisticsDTO: caseNewToday){
            DailyStatistics daily = dailyStatisticsMap.get(statisticsDTO.getProjectId());
            if(statisticsDTO.getName().equals("API")){
                daily.setApiCaseNew(statisticsDTO.getCount());
            }else if(statisticsDTO.getName().equals("WEB")){
                daily.setWebCaseNew(statisticsDTO.getCount());
            }else {
                daily.setAppCaseNew(statisticsDTO.getCount());
            }
        }
        List<StatisticsDTO> caseNewWeek = statisticsMapper.getCaseWeekNewCountByProject();
        for (StatisticsDTO statisticsDTO: caseNewWeek){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            if (statisticsDTO.getName().equals("API")){
                sum.setApiCaseNewWeek(statisticsDTO.getCount());
            }else if(statisticsDTO.getName().equals("WEB")){
                sum.setWebCaseNewWeek(statisticsDTO.getCount());
            }else {
                sum.setAppCaseNewWeek(statisticsDTO.getCount());
            }
        }
        List<StatisticsDTO> caseRunToday = statisticsMapper.getCaseTodayRunCountByProject();
        for (StatisticsDTO statisticsDTO: caseRunToday){
            DailyStatistics daily = dailyStatisticsMap.get(statisticsDTO.getProjectId());
            if (statisticsDTO.getName().equals("API")){
                daily.setApiCaseRun(statisticsDTO.getCount());
                daily.setApiCasePassRate(statisticsDTO.getPassRate());
            }else if(statisticsDTO.getName().equals("WEB")){
                daily.setWebCaseRun(statisticsDTO.getCount());
                daily.setWebCasePassRate(statisticsDTO.getPassRate());
            }else {
                daily.setAppCaseRun(statisticsDTO.getCount());
                daily.setAppCasePassRate(statisticsDTO.getPassRate());
            }
        }
        List<StatisticsDTO> caseRunTotal = statisticsMapper.getCaseTotalRunCountByProject();
        for (StatisticsDTO statisticsDTO: caseRunTotal){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            sum.setCaseRunTotal(statisticsDTO.getCount());
        }
        List<StatisticsDTO> caseRunTotalToday = statisticsMapper.getCaseTotalTodayRunCountByProject();
        for (StatisticsDTO statisticsDTO: caseRunTotalToday){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            sum.setCaseRunToday(statisticsDTO.getCount());
        }
        List<StatisticsDTO> planRunTop = statisticsMapper.getPlanRunTopByProject();
        for (StatisticsDTO statisticsDTO: planRunTop){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            JSONObject planRunObj = JSONObject.parseObject(sum.getPlanRunWeekTop());
            planRunObj.getJSONArray("xAxis").add(statisticsDTO.getName());
            planRunObj.getJSONArray("planRunTotal").add(statisticsDTO.getCount());
            planRunObj.getJSONArray("planRunPass").add(statisticsDTO.getPass());
            planRunObj.getJSONArray("planRunPassRate").add(statisticsDTO.getPassRate());
            if (planRunObj.getInteger("yMaxLeft") < statisticsDTO.getCount()){
                planRunObj.put("yMaxLeft", statisticsDTO.getCount());
            }
            sum.setPlanRunWeekTop(planRunObj.toString());
        }
        List<StatisticsDTO> caseFailTop = statisticsMapper.getCaseFailTopByProject();
        for (StatisticsDTO statisticsDTO: caseFailTop){
            SumStatistics sum = sumStatisticsMap.get(statisticsDTO.getProjectId());
            JSONObject caseFailObj = JSONObject.parseObject(sum.getCaseFailWeekTop());
            caseFailObj.getJSONArray("x").add(statisticsDTO.getCount());
            caseFailObj.getJSONArray("y").add(statisticsDTO.getName());
            sum.setCaseFailWeekTop(caseFailObj.toString());
        }
        List<SumStatistics> sumStatisticsList = new ArrayList<>(sumStatisticsMap.values());
        statisticsMapper.updateSumData(sumStatisticsList);
        List<DailyStatistics> dailyStatisticsList = new ArrayList<>(dailyStatisticsMap.values());
        statisticsMapper.updateDailyData(dailyStatisticsList);
    }
}
