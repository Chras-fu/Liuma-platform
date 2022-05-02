package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Report;
import com.autotest.LiuMa.database.domain.ReportStatistics;
import com.autotest.LiuMa.dto.ReportDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    void addReport(Report report);

    void addReportStatistics(ReportStatistics reportStatistics);

    ReportStatistics getReportStatistics(String reportId);

    List<Report> selectTimeoutReport(Long minLastUploadTime, Long minLastToRunTime);

    void updateReportStartTime(String reportId, Long startTime, Long updateTime);

    void updateReportEndTime(String reportId, Long endTime, Long updateTime);

    void updateReportStatistics(ReportStatistics reportStatistics);

    void deleteReport(String id);

    void updateReportStatus(String status, String id);

    void updateReportStatusByTask(String status, String taskId);

    void updateAllReportStatusByEngine(String status, String engineId);

    List<ReportDTO> getReportList(QueryRequest request);

    ReportDTO getReportDetail(String reportId);

}