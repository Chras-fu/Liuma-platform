package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.ReportSourceType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.request.RunRequest;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class RunService {
    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private PlanCollectionMapper planCollectionMapper;

    @Resource
    private CollectionCaseMapper collectionCaseMapper;

    @Resource
    private DebugDataMapper debugDataMapper;

    public Task run(RunRequest runRequest) {
        String runName = runRequest.getSourceName() +"-"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 调试执行则存储临时数据
        if(runRequest.getSourceType().equals(ReportSourceType.TEMP.toString())){
            DebugData debugData = new DebugData();
            debugData.setId(UUID.randomUUID().toString());
            debugData.setData(JSONObject.toJSONString(runRequest.getDebugData()));
            debugDataMapper.addDebugData(debugData);
            runRequest.setSourceId(debugData.getId());
        }
        // 新增任务
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setName(runName);
        task.setStatus(ReportStatus.PREPARED.toString());
        task.setType(runRequest.getTaskType());
        task.setEngineId(runRequest.getEngineId());
        task.setProjectId(runRequest.getProjectId());
        task.setCreateUser(runRequest.getRunUser());
        task.setUpdateUser(runRequest.getRunUser());
        task.setCreateTime(System.currentTimeMillis());
        task.setUpdateTime(System.currentTimeMillis());
        taskMapper.addTask(task);
        // 预设报告
        Report report = new Report();
        report.setId(UUID.randomUUID().toString());
        report.setName(runName);
        report.setTaskId(task.getId());
        report.setEnvironmentId(runRequest.getEnvironmentId());
        report.setSourceType(runRequest.getSourceType());
        report.setSourceId(runRequest.getSourceId());
        report.setStatus(ReportStatus.PREPARED.toString());
        report.setProjectId(runRequest.getProjectId());
        report.setCreateUser(runRequest.getRunUser());
        report.setUpdateUser(runRequest.getRunUser());
        report.setCreateTime(System.currentTimeMillis());
        report.setUpdateTime(System.currentTimeMillis());
        reportMapper.addReport(report);
        // 统计报告用例数
        ReportStatistics reportStatistics = new ReportStatistics();
        reportStatistics.setId(UUID.randomUUID().toString());
        reportStatistics.setReportId(report.getId());
        reportStatistics.setPassCount(0);
        reportStatistics.setErrorCount(0);
        reportStatistics.setFailCount(0);
        Integer total = 0;
        if(runRequest.getSourceType().equals(ReportSourceType.PLAN.toString())){
            total = planCollectionMapper.getPlanCaseCount(runRequest.getSourceId());
        }else if(runRequest.getSourceType().equals(ReportSourceType.COLLECTION.toString())){
            total = collectionCaseMapper.getCollectionCaseCount(runRequest.getSourceId());
        }else {
            total = 1;
        }
        reportStatistics.setTotal(total);
        reportMapper.addReportStatistics(reportStatistics);
        return task;
    }

}
