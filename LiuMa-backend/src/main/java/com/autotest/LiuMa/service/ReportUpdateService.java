package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONArray;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.CollectionDTO;
import com.autotest.LiuMa.dto.TaskDTO;
import com.autotest.LiuMa.request.CaseResultRequest;
import com.autotest.LiuMa.request.TransResultRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReportUpdateService {

    @Value("${qiniu.cloud.downloadUrl}")
    private String downloadUrl;  // 七牛云加速域名

    @Value("${cloud.storage.on-off}")
    private String cloudStorage;  // 云存储开关

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    ReportCollectionMapper reportCollectionMapper;

    @Resource
    ReportCollectionCaseMapper reportCollectionCaseMapper;

    @Resource
    ReportCollectionCaseApiMapper reportCollectionCaseApiMapper;

    @Resource
    ReportCollectionCaseWebMapper reportCollectionCaseWebMapper;

    @Resource
    ReportCollectionCaseAppMapper reportCollectionCaseAppMapper;

    public void updateReport(TaskDTO task, List<CaseResultRequest> caseResultList) {
        // 更新报告 每次可更新多条用例
        for(CaseResultRequest caseResult:caseResultList){
            ReportCollection reportCollection = reportCollectionMapper.getReportCollection(task.getReportId(), caseResult.getCollectionId());
            if(reportCollection == null){   // 如果报告集合没有则创建
                CollectionDTO collection = collectionMapper.getCollectionDetail(caseResult.getCollectionId());
                reportCollection = new ReportCollection();
                reportCollection.setId(UUID.randomUUID().toString());
                reportCollection.setReportId(task.getReportId());
                reportCollection.setCollectionId(caseResult.getCollectionId());
                if (collection !=null) {
                    reportCollection.setCollectionName(collection.getName());
                    reportCollection.setCollectionVersion(collection.getVersionName());
                    reportCollection.setCaseTotal(collectionCaseMapper.getCollectionCaseCount(caseResult.getCollectionId()));
                }else {
                    reportCollection.setCollectionName("");
                    reportCollection.setCollectionVersion("");
                    reportCollection.setCaseTotal(1);
                }
                reportCollectionMapper.addReportCollection(reportCollection);
            }
            // 插入报告用例表
            ReportCollectionCase reportCollectionCase = new ReportCollectionCase();
            reportCollectionCase.setId(UUID.randomUUID().toString());
            reportCollectionCase.setReportCollectionId(reportCollection.getId());
            reportCollectionCase.setCollectionCaseIndex(caseResult.getIndex());
            reportCollectionCase.setCaseId(caseResult.getCaseId());
            reportCollectionCase.setCaseType(caseResult.getCaseType());
            reportCollectionCase.setCaseName(caseResult.getCaseName());
            reportCollectionCase.setCaseDesc(caseResult.getCaseDesc());
            reportCollectionCase.setStartTime(caseResult.getStartTime());
            reportCollectionCase.setEndTime(caseResult.getEndTime());
            reportCollectionCase.setRunTimes(caseResult.getRunTimes());
            reportCollectionCase.setDuring((caseResult.getEndTime()-caseResult.getStartTime()) + "ms");
            reportCollectionCase.setStatus(getStatusByIndex(caseResult.getStatus()));
            reportCollectionCaseMapper.addReportCollectionCase(reportCollectionCase);
            // 插入报告接口/web表
            if(caseResult.getCaseType().equals("API")){
                List<ReportCollectionCaseApi> reportCollectionCaseApiList = new ArrayList<>();
                for(int index=1; index <= caseResult.getTransactionList().size(); index++){
                    TransResultRequest transactionResult =caseResult.getTransactionList().get(index-1);
                    ReportCollectionCaseApi reportCollectionCaseApi = new ReportCollectionCaseApi();
                    reportCollectionCaseApi.setId(UUID.randomUUID().toString());
                    reportCollectionCaseApi.setReportCollectionCaseId(reportCollectionCase.getId());
                    reportCollectionCaseApi.setCaseApiIndex(index);
                    reportCollectionCaseApi.setApiId(transactionResult.getId());
                    reportCollectionCaseApi.setApiName(transactionResult.getName());
                    reportCollectionCaseApi.setApiPath(transactionResult.getContent());
                    reportCollectionCaseApi.setDescription(transactionResult.getDescription());
                    reportCollectionCaseApi.setExecLog(transactionResult.getLog());
                    reportCollectionCaseApi.setDuring(transactionResult.getDuring());
                    reportCollectionCaseApi.setStatus(getStatusByIndex(transactionResult.getStatus()));
                    reportCollectionCaseApiList.add(reportCollectionCaseApi);
                }
                reportCollectionCaseApiMapper.batchAddReportCollectionCaseApi(reportCollectionCaseApiList);
            }else if(caseResult.getCaseType().equals("WEB")){
                List<ReportCollectionCaseWeb> reportCollectionCaseWebList = new ArrayList<>();
                for(int index=1; index <= caseResult.getTransactionList().size(); index++){
                    TransResultRequest transactionResult =caseResult.getTransactionList().get(index-1);
                    ReportCollectionCaseWeb reportCollectionCaseWeb = new ReportCollectionCaseWeb();
                    reportCollectionCaseWeb.setId(UUID.randomUUID().toString());
                    reportCollectionCaseWeb.setReportCollectionCaseId(reportCollectionCase.getId());
                    reportCollectionCaseWeb.setCaseWebIndex(index);
                    reportCollectionCaseWeb.setOperationId(transactionResult.getId());
                    reportCollectionCaseWeb.setOperationName(transactionResult.getName());
                    reportCollectionCaseWeb.setOperationElement(transactionResult.getContent());
                    reportCollectionCaseWeb.setDescription(transactionResult.getDescription());
                    reportCollectionCaseWeb.setExecLog(transactionResult.getLog());
                    List<String> screenshot = new ArrayList<>();
                    for(String screenshotId:transactionResult.getScreenShotList()){
                        String url;
                        if(cloudStorage.equals("on")){
                            url = downloadUrl + "/" + screenshotId + ".png";
                        }else {
                            url = "/openapi/screenshot/" + screenshotId.split("_")[0] +
                                    "/" + screenshotId.split("_")[1] + ".png";
                        }
                        screenshot.add(url);
                    }
                    reportCollectionCaseWeb.setScreenshot(JSONArray.toJSONString(screenshot));
                    reportCollectionCaseWeb.setStatus(getStatusByIndex(transactionResult.getStatus()));
                    reportCollectionCaseWebList.add(reportCollectionCaseWeb);
                }
                reportCollectionCaseWebMapper.batchAddReportCollectionCaseWeb(reportCollectionCaseWebList);
            }else {
                List<ReportCollectionCaseApp> reportCollectionCaseAppList = new ArrayList<>();
                for(int index=1; index <= caseResult.getTransactionList().size(); index++){
                    TransResultRequest transactionResult =caseResult.getTransactionList().get(index-1);
                    ReportCollectionCaseApp reportCollectionCaseApp = new ReportCollectionCaseApp();
                    reportCollectionCaseApp.setId(UUID.randomUUID().toString());
                    reportCollectionCaseApp.setReportCollectionCaseId(reportCollectionCase.getId());
                    reportCollectionCaseApp.setCaseAppIndex(index);
                    reportCollectionCaseApp.setOperationId(transactionResult.getId());
                    reportCollectionCaseApp.setOperationName(transactionResult.getName());
                    reportCollectionCaseApp.setOperationElement(transactionResult.getContent());
                    reportCollectionCaseApp.setDescription(transactionResult.getDescription());
                    reportCollectionCaseApp.setExecLog(transactionResult.getLog());
                    List<String> screenshot = new ArrayList<>();
                    for(String screenshotId:transactionResult.getScreenShotList()){
                        String url;
                        if(cloudStorage.equals("on")){
                            url = downloadUrl + "/" + screenshotId + ".png";
                        }else {
                            url = "/openapi/screenshot/" + screenshotId.split("_")[0] +
                                    "/" + screenshotId.split("_")[1] + ".png";
                        }
                        screenshot.add(url);
                    }
                    reportCollectionCaseApp.setScreenshot(JSONArray.toJSONString(screenshot));
                    reportCollectionCaseApp.setStatus(getStatusByIndex(transactionResult.getStatus()));
                    reportCollectionCaseAppList.add(reportCollectionCaseApp);
                }
                reportCollectionCaseAppMapper.batchAddReportCollectionCaseApp(reportCollectionCaseAppList);
            }
        }
        // 更新报告时间
        reportMapper.updateReportEndTime(task.getReportId(), System.currentTimeMillis(), System.currentTimeMillis());
        // 统计报告 后续可job统计
        this.statisticsReport(task.getReportId());
    }

    public void statisticsReport(String reportId){
        Integer passCount = reportCollectionCaseMapper.countReportResult(ReportStatus.SUCCESS.toString(), reportId);
        Integer failCount = reportCollectionCaseMapper.countReportResult(ReportStatus.FAIL.toString(), reportId);
        Integer errorCount = reportCollectionCaseMapper.countReportResult(ReportStatus.ERROR.toString(), reportId);
        ReportStatistics reportStatistics = reportMapper.getReportStatistics(reportId);
        reportStatistics.setPassCount(passCount);
        reportStatistics.setFailCount(failCount);
        reportStatistics.setErrorCount(errorCount);
        reportMapper.updateReportStatistics(reportStatistics);
    }

    private String getStatusByIndex(Integer status) {
        if(status == 0){
            return ReportStatus.SUCCESS.toString();
        }else if (status == 1){
            return ReportStatus.FAIL.toString();
        }else if (status == 2){
            return ReportStatus.ERROR.toString();
        }else {
            return ReportStatus.SKIP.toString();
        }
    }

}
