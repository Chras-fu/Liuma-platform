package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.PlanCollection;
import com.autotest.LiuMa.dto.PlanCollectionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanCollectionMapper {
    void addPlanCollection(List<PlanCollection> planCollections);

    void deletePlanCollection(String planId);

    List<PlanCollectionDTO> getPlanCollectionList(String planId);

    Integer getPlanCaseCount(String planId);
}