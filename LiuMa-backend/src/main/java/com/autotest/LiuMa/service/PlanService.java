package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.PlanFrequency;
import com.autotest.LiuMa.database.domain.Plan;
import com.autotest.LiuMa.database.domain.PlanCollection;
import com.autotest.LiuMa.database.domain.PlanSchedule;
import com.autotest.LiuMa.database.mapper.PlanCollectionMapper;
import com.autotest.LiuMa.database.mapper.PlanMapper;
import com.autotest.LiuMa.database.mapper.PlanScheduleMapper;
import com.autotest.LiuMa.dto.PlanCollectionDTO;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void savePlan(PlanDTO planDTO) {
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
        planCollectionMapper.deletePlanCollection(planDTO.getId());  //先删除全部计划集合
        List<PlanCollection> planCollections = new ArrayList<>();
        for(PlanCollectionDTO planCollectionDTO: planDTO.getPlanCollections()){
            PlanCollection planCollection = new PlanCollection();
            planCollection.setId(UUID.randomUUID().toString());
            planCollection.setPlanId(planDTO.getId());
            planCollection.setCollectionId(planCollectionDTO.getCollectionId());
            planCollections.add(planCollection);
        }
        planCollectionMapper.addPlanCollection(planCollections);
    }

    public void deletePlan(Plan plan) {
        planCollectionMapper.deletePlanCollection(plan.getId());
        planScheduleMapper.deletePlanSchedule(plan.getId());
        planMapper.deletePlan(plan.getId());
    }

    public PlanDTO getPlanDetail(String planId) {
        PlanDTO planDTO = planMapper.getPlanDetail(planId);
        List<PlanCollectionDTO> planCollectionDTOS = planCollectionMapper.getPlanCollectionList(planId);
        planDTO.setPlanCollections(planCollectionDTOS);

        return planDTO;
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
