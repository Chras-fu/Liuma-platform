package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ReportCollectionCaseApi;
import com.autotest.LiuMa.dto.ReportCollectionCaseTransDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportCollectionCaseApiMapper {
    void batchAddReportCollectionCaseApi(List<ReportCollectionCaseApi> reportCollectionCaseApis);

    List<ReportCollectionCaseTransDTO> getReportCaseActionList(String reportCaseId);

    String getLastApiReport(String apiId);

    void deleteByReportId(String reportId);
}