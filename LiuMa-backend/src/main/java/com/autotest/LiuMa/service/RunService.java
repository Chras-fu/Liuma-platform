package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.DeviceStatus;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.PlanCollectionDTO;
import com.autotest.LiuMa.dto.TaskDTO;
import com.autotest.LiuMa.request.RunRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class RunService {
    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private DebugDataMapper debugDataMapper;

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private DeviceService deviceService;

    public Task run(RunRequest runRequest) {
        // 新增任务
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        // 判断app用例的设备是否可用
        if(runRequest.getDeviceId() != null && !runRequest.getDeviceId().equals("")){
            Device device = deviceMapper.getDeviceById(runRequest.getDeviceId());
            if((!device.getStatus().equals(DeviceStatus.ONLINE.toString())) &&
                    (!(device.getStatus().equals(DeviceStatus.USING.toString()) &&
                            runRequest.getRunUser().equals(device.getUser())))){
                // 设备空闲中或者设备在使用中且使用者是自己可以执行单个用例
                throw new LMException("设备非空闲状态 执行失败");
            }
            if(device.getStatus().equals(DeviceStatus.ONLINE.toString())) {
                // 使用中的设备执行测试不予修改状态 空闲中则进行修改
                device.setStatus(DeviceStatus.TESTING.toString());
                device.setUpdateTime(System.currentTimeMillis());
                device.setUser(task.getId());   // 测试中的设备使用者是任务id
                device.setTimeout(-1);  // 测试中设备不予超时
                deviceMapper.updateDevice(device);  // 占用设备
            }
        }
        // 调试执行则存储临时数据
        if(runRequest.getSourceType().equals(ReportSourceType.TEMP.toString())){
            DebugData debugData = new DebugData();
            debugData.setId(UUID.randomUUID().toString());
            debugData.setData(JSONObject.toJSONString(runRequest.getDebugData()));
            debugDataMapper.addDebugData(debugData);
            runRequest.setSourceId(debugData.getId());
        }
        String runName = runRequest.getSourceName() +"-"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        task.setName(runName);
        task.setStatus(ReportStatus.PREPARED.toString());
        task.setType(runRequest.getTaskType());
        task.setEngineId(runRequest.getEngineId());
        task.setProjectId(runRequest.getProjectId());
        task.setCreateUser(runRequest.getRunUser());
        task.setUpdateUser(runRequest.getRunUser());
        task.setCreateTime(System.currentTimeMillis());
        task.setUpdateTime(System.currentTimeMillis());
        taskMapper.addTask(task);
        // 预设报告
        Report report = new Report();
        report.setId(UUID.randomUUID().toString());
        report.setName(runName);
        report.setTaskId(task.getId());
        report.setEnvironmentId(runRequest.getEnvironmentId());
        report.setDeviceId(runRequest.getDeviceId());
        report.setSourceType(runRequest.getSourceType());
        report.setSourceId(runRequest.getSourceId());
        report.setStatus(ReportStatus.PREPARED.toString());
        report.setProjectId(runRequest.getProjectId());
        report.setCreateUser(runRequest.getRunUser());
        report.setUpdateUser(runRequest.getRunUser());
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
        Integer total = 0;
        if(runRequest.getSourceType().equals(ReportSourceType.PLAN.toString())){
            total = planCollectionMapper.getPlanCaseCount(runRequest.getSourceId());
        }else if(runRequest.getSourceType().equals(ReportSourceType.COLLECTION.toString())){
            total = collectionCaseMapper.getCollectionCaseCount(runRequest.getSourceId());
        }else {
            total = 1;
        }
        reportStatistics.setTotal(total);
        reportMapper.addReportStatistics(reportStatistics);
        return task;
    }

    public void stopDeviceWhenRunEnd(String taskId){
        TaskDTO task = taskMapper.getTaskDetail(taskId);
        if(task.getDeviceId() != null) { // 单用例执行
            Device device = deviceService.getDeviceDetail(task.getDeviceId());
            if(!(device.getStatus().equals(DeviceStatus.USING.toString())
                    && task.getCreateUser().equals(device.getUser()))){
                deviceService.coldDevice(device);    // 非占用中状态调试则释放设备
            }
        }else {
            if(task.getSourceType().equals(ReportSourceType.COLLECTION.toString())){
                Collection collection = collectionMapper.getCollectionDetail(task.getSourceId());
                if(collection == null) return;
                if(collection.getDeviceId() != null){
                    Device device = deviceService.getDeviceDetail(collection.getDeviceId());
                    if(device.getStatus().equals(DeviceStatus.TESTING.toString()) &&
                            task.getId().equals(device.getUser())){
                        deviceService.coldDevice(device); //当前设备使用者仍然是该任务才会停用
                    }
                }
            }else if(task.getSourceType().equals(ReportSourceType.PLAN.toString())){
                List<PlanCollectionDTO> planCollections = planCollectionMapper.getPlanCollectionList(task.getSourceId());
                for(PlanCollectionDTO planCollectionDTO:planCollections){
                    Collection collection = collectionMapper.getCollectionDetail(planCollectionDTO.getCollectionId());
                    if(collection==null) return;
                    if(collection.getDeviceId() != null){
                        Device device = deviceService.getDeviceDetail(collection.getDeviceId());
                        if(device.getStatus().equals(DeviceStatus.TESTING.toString()) &&
                                task.getId().equals(device.getUser())){
                            deviceService.coldDevice(device); //当前设备使用者仍然是该任务才会停用
                        }
                    }
                }
            }
        }
    }

}
