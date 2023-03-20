package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Plan;
import com.autotest.LiuMa.database.domain.PlanSchedule;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanScheduleMapper {
    void addPlanSchedule(PlanSchedule planSchedule);

    void updatePlanSchedule(PlanSchedule planSchedule);

    PlanSchedule getPlanSchedule(String planId);

    List<PlanSchedule> getToRunPlanScheduleList(Long minNextRunTime, Long maxNextRunTime);
}