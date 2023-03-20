package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ReportCollectionCase;
import com.autotest.LiuMa.dto.ReportCollectionCaseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportCollectionCaseMapper {
    void addReportCollectionCase(ReportCollectionCase reportCollectionCase);

    Integer countReportResult(String status, String reportId);

    Integer countReportCollectionResult(String status, String reportCollectionId);

    ReportCollectionCaseDTO getCaseReportByTaskId(String taskId);

    List<ReportCollectionCaseDTO> getReportCollectionCaseList(String reportCollectionId);

    void deleteByReportId(String reportId);
}