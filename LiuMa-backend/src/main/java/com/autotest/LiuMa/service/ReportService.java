package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.database.domain.Report;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.ReportCollectionCaseDTO;
import com.autotest.LiuMa.dto.ReportCollectionCaseTransDTO;
import com.autotest.LiuMa.dto.ReportCollectionDTO;
import com.autotest.LiuMa.dto.ReportDTO;
import com.autotest.LiuMa.request.QueryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReportService {

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private ReportCollectionMapper reportCollectionMapper;

    @Resource
    private ReportCollectionCaseMapper reportCollectionCaseMapper;

    @Resource
    private ReportCollectionCaseApiMapper reportCollectionCaseApiMapper;

    @Resource
    private ReportCollectionCaseWebMapper reportCollectionCaseWebMapper;

    @Resource
    private ReportCollectionCaseAppMapper reportCollectionCaseAppMapper;

    public void deleteReport(Report report) {
        reportCollectionCaseAppMapper.deleteByReportId(report.getId());
        reportCollectionCaseApiMapper.deleteByReportId(report.getId());
        reportCollectionCaseWebMapper.deleteByReportId(report.getId());
        reportCollectionCaseMapper.deleteByReportId(report.getId());
        reportCollectionMapper.deleteByReportId(report.getId());
        reportMapper.deleteReport(report.getId());
    }

    public List<ReportDTO> getReportList(QueryRequest request){
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition(("%"+request.getCondition()+"%"));
        }
        return reportMapper.getReportList(request);
    }

    public JSONObject getLastApiReport(String apiId){
        JSONObject result = new JSONObject();
        String report = reportCollectionCaseApiMapper.getLastApiReport(apiId);
        if(report == null){
            return null;
        }
        if(!report.contains("<br><b>响应体: ") || !report.contains("</b><br><br>")){
            return null;
        }
        String response = report.substring(report.indexOf("<br><b>响应体: ")+12, report.indexOf("</b><br><br>"));
        try{
            result = JSONObject.parseObject(response);
        }catch (Exception e){
            return result;
        }
        return result;
    }

    public ReportCollectionCaseDTO getCaseResult(String taskId){
        ReportCollectionCaseDTO reportCase = reportCollectionCaseMapper.getCaseReportByTaskId(taskId);
        if(reportCase != null){
            List<ReportCollectionCaseTransDTO> transList;
            if(reportCase.getCaseType().equals("API")){
                transList = reportCollectionCaseApiMapper.getReportCaseActionList(reportCase.getId());
            }else if(reportCase.getCaseType().equals("WEB")){
                transList = reportCollectionCaseWebMapper.getReportCaseActionList(reportCase.getId());
            }else {
                transList = reportCollectionCaseAppMapper.getReportCaseActionList(reportCase.getId());
            }
            reportCase.setTransList(transList);
        }

        return reportCase;
    }

    public ReportDTO getPlanResult(String reportId){
        ReportDTO report = reportMapper.getReportDetail(reportId);
        List<ReportCollectionDTO> reportCollectionList = reportCollectionMapper.getReportCollectionList(reportId);
        for(ReportCollectionDTO reportCollection:reportCollectionList){
            List<ReportCollectionCaseDTO> reportCollectionCaseList = reportCollectionCaseMapper.getReportCollectionCaseList(reportCollection.getId());
            for(ReportCollectionCaseDTO reportCollectionCase: reportCollectionCaseList){
                List<ReportCollectionCaseTransDTO> transList;
                if(reportCollectionCase.getCaseType().equals("API")){
                    transList = reportCollectionCaseApiMapper.getReportCaseActionList(reportCollectionCase.getId());
                }else if(reportCollectionCase.getCaseType().equals("WEB")){
                    transList = reportCollectionCaseWebMapper.getReportCaseActionList(reportCollectionCase.getId());
                }else {
                    transList = reportCollectionCaseAppMapper.getReportCaseActionList(reportCollectionCase.getId());
                }
                reportCollectionCase.setTransList(transList);
            }
            reportCollection.setCaseList(reportCollectionCaseList);
            Integer passCount = reportCollectionCaseMapper.countReportCollectionResult(ReportStatus.SUCCESS.toString(), reportCollection.getId());
            Integer failCount = reportCollectionCaseMapper.countReportCollectionResult(ReportStatus.FAIL.toString(), reportCollection.getId());
            Integer errorCount = reportCollectionCaseMapper.countReportCollectionResult(ReportStatus.ERROR.toString(), reportCollection.getId());
            reportCollection.setPassCount(passCount);
            reportCollection.setFailCount(failCount);
            reportCollection.setErrorCount(errorCount);
        }
        report.setCollectionList(reportCollectionList);
        return report;
    }
}
