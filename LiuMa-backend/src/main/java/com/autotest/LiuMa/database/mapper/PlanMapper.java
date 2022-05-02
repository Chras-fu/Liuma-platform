package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Plan;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanMapper {
    void addPlan(Plan plan);

    void updatePlan(Plan plan);

    void deletePlan(String id);

    PlanDTO getPlanDetail(String id);

    List<PlanDTO> getPlanList(QueryRequest request);
}