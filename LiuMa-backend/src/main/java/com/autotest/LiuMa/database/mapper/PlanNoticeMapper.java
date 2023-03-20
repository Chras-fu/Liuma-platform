package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.PlanNotice;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PlanNoticeMapper {
    void addPlanNotice(PlanNotice planNotice);

    void updatePlanNotice(PlanNotice planNotice);

    PlanNotice getPlanNotice(String planId);
}