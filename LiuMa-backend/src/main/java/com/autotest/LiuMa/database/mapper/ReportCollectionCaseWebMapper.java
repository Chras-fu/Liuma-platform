package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.ReportCollectionCaseWeb;
import com.autotest.LiuMa.dto.ReportCollectionCaseTransDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportCollectionCaseWebMapper {
    void batchAddReportCollectionCaseWeb(List<ReportCollectionCaseWeb> reportCollectionCaseWebs);

    List<ReportCollectionCaseTransDTO> getReportCaseActionList(String reportCaseId);

    void deleteByReportId(String reportId);
}