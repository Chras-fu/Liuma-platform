package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ReportCollectionCaseApp;
import com.autotest.LiuMa.dto.ReportCollectionCaseTransDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportCollectionCaseAppMapper {
    void batchAddReportCollectionCaseApp(List<ReportCollectionCaseApp> reportCollectionCaseApps);

    List<ReportCollectionCaseTransDTO> getReportCaseActionList(String reportCaseId);
}