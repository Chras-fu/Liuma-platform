package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.PlanFrequency;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.database.domain.Plan;
import com.autotest.LiuMa.database.domain.PlanCollection;
import com.autotest.LiuMa.database.domain.PlanNotice;
import com.autotest.LiuMa.database.domain.PlanSchedule;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.PlanCollectionDTO;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class PlanService {

    @Resource
    private PlanMapper planMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private PlanScheduleMapper planScheduleMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private PlanNoticeMapper planNoticeMapper;

    public void savePlan(PlanDTO planDTO) {
        if(planDTO.getEnvironmentId() == null || planDTO.getEnvironmentId().equals("")){ // 如果环境未选 则判断每个集合是否都没有API用例和WEB用例
            for(PlanCollectionDTO planCollectionDTO: planDTO.getPlanCollections()){
                List<String> caseTypes = collectionCaseMapper.getCollectionCaseTypes(planCollectionDTO.getCollectionId());
                if(caseTypes.contains(null)){
                    throw new LMException("所选集合中存在API或WEB用例 环境不能为空");
                }
            }
        }
        if(planDTO.getId().equals("") || planDTO.getId() == null){ // 新增计划
            planDTO.setId(UUID.randomUUID().toString());
            planDTO.setCreateTime(System.currentTimeMillis());
            planDTO.setUpdateTime(System.currentTimeMillis());
            planDTO.setCreateUser(planDTO.getUpdateUser());
            planMapper.addPlan(planDTO);
            PlanSchedule planSchedule = new PlanSchedule();
            planSchedule.setId(UUID.randomUUID().toString());
            planSchedule.setPlanId(planDTO.getId());
            planSchedule.setStartTime(planDTO.getStartTime());
            planSchedule.setFrequency(planDTO.getFrequency());
            planSchedule.setNextRunTime(convertStrToTime(planDTO.getStartTime()));
            planScheduleMapper.addPlanSchedule(planSchedule);
        }else{ // 修改计划
            planDTO.setUpdateTime(System.currentTimeMillis());
            PlanSchedule planSchedule = planScheduleMapper.getPlanSchedule(planDTO.getId());
            if(!planSchedule.getStartTime().equals(planDTO.getStartTime())){
                // 修改了开始时间 开始时间一定大于当前时间
                planSchedule.setStartTime(planDTO.getStartTime());
                planSchedule.setFrequency(planDTO.getFrequency());
                Long startTime = convertStrToTime(planSchedule.getStartTime());
                planSchedule.setNextRunTime(startTime);
                planScheduleMapper.updatePlanSchedule(planSchedule);
            }else if(!planSchedule.getFrequency().equals(planDTO.getFrequency())){
                // 没有修改开始时间但是修改了执行频率
                planSchedule.setFrequency(planDTO.getFrequency());
                Long startTime = convertStrToTime(planSchedule.getStartTime());
                while (!planDTO.getFrequency().equals(PlanFrequency.ONLY_ONE.toString()) && startTime < System.currentTimeMillis()){ // 找到大于当前时间的日期
                    startTime = getNextRunTime(startTime, planSchedule.getFrequency());
                }
                planSchedule.setNextRunTime(startTime);
                planScheduleMapper.updatePlanSchedule(planSchedule);
            }
            planMapper.updatePlan(planDTO);
        }
        List<PlanCollection> planCollections = new ArrayList<>();
        for(PlanCollectionDTO planCollectionDTO: planDTO.getPlanCollections()){
            PlanCollection planCollection = new PlanCollection();
            planCollection.setId(UUID.randomUUID().toString());
            planCollection.setPlanId(planDTO.getId());
            planCollection.setCollectionId(planCollectionDTO.getCollectionId());
            planCollections.add(planCollection);
        }
        planCollectionMapper.deletePlanCollection(planDTO.getId());  //先删除全部计划集合
        if(planCollections.size() > 0) {
            try {
                planCollectionMapper.addPlanCollection(planCollections);
            }catch (Exception e){
                throw new LMException("一个测试计划不能重复选择同一测试集合");
            }
        }
    }

    public void savePlanNotice(PlanNotice planNotice){
        if(planNotice.getId() == null || planNotice.getId().equals("")){
            planNotice.setId(UUID.randomUUID().toString());
            planNoticeMapper.addPlanNotice(planNotice);
        }else {
            planNoticeMapper.updatePlanNotice(planNotice);
        }
    }

    public void deletePlan(Plan plan) {
        planMapper.deletePlan(plan.getId());
    }

    public PlanDTO getPlanDetail(String planId) {
        PlanDTO planDTO = planMapper.getPlanDetail(planId);
        List<PlanCollectionDTO> planCollectionDTOS = planCollectionMapper.getPlanCollectionList(planId);
        planDTO.setPlanCollections(planCollectionDTOS);

        return planDTO;
    }

    public PlanNotice getPlanNotice(String planId) {
        return planNoticeMapper.getPlanNotice(planId);
    }

    public List<PlanDTO> getPlanList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        return planMapper.getPlanList(request);
    }

    public static Long convertStrToTime(String time){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date date = dateFormat.parse(time);
            return date.getTime();
        }catch (Exception e){
            return 0L;
        }
    }

    public static Long getNextRunTime(Long lastTime, String frequency){
        if(frequency.equals(PlanFrequency.HALF_HOUR.toString())){
            return lastTime + 30*60*1000;
        }else if (frequency.equals(PlanFrequency.ONE_HOUR.toString())){
            return lastTime + 60*60*1000;
        }else if (frequency.equals(PlanFrequency.HALF_DAY.toString())){
            return lastTime + 12*60*60*1000;
        }else if (frequency.equals(PlanFrequency.ONE_DAY.toString())){
            return lastTime + 24*60*60*1000;
        }else if (frequency.equals(PlanFrequency.ONE_WEEK.toString())){
            return lastTime + 7*24*60*60*1000;
        }else if (frequency.equals(PlanFrequency.ONE_MONTH.toString())){
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(lastTime));
            c.add(Calendar.MONTH, 1);
            return c.getTimeInMillis();
        }else {
            return lastTime;
        }
    }
}
