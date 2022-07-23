package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.*;
import com.autotest.LiuMa.common.exception.EngineVerifyException;
import com.autotest.LiuMa.common.exception.LMException;
import com.autotest.LiuMa.common.utils.EmailUtils;
import com.autotest.LiuMa.common.utils.FileUtils;
import com.autotest.LiuMa.common.utils.JwtUtils;
import com.autotest.LiuMa.common.utils.UploadUtils;
import com.autotest.LiuMa.database.domain.*;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.TaskDTO;
import com.autotest.LiuMa.request.CaseResultRequest;
import com.autotest.LiuMa.request.EngineRequest;
import com.autotest.LiuMa.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class OpenApiService {

    @Value("${task.file.path}")
    public String TASK_FILE_PATH;

    @Value("${qiniu.cloud.ak}")
    private String ak;   // 七牛云ak

    @Value("${qiniu.cloud.sk}")
    private String sk;    // 七牛云sk

    @Value("${qiniu.cloud.bucket}")
    private String imageBucket;    // 七牛云空间名

    @Value("${qiniu.cloud.uploadUrl}")
    private String uploadUrl;   // 七牛云上传域名

    @Value("${aliyun.email.accessKey}")
    private String accessKey;    // 阿里云邮件key

    @Value("${aliyun.email.accessSecret}")
    private String accessSecret;     // 阿里云邮件secret

    @Value("${aliyun.email.runnerSenderAddress}")
    private String runnerSenderAddress;  // 发送人邮箱地址

    @Value("${aliyun.email.runnerSenderName}")
    private String runnerSenderName;

    @Resource
    private UserMapper userMapper;

    @Resource
    private EngineMapper engineMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    @Resource
    private PlanMapper planMapper;

    @Resource
    private TestFileMapper testFileMapper;

    @Resource
    private CaseJsonCreateService caseJsonCreateService;

    @Resource
    private ReportUpdateService reportUpdateService;

    @Resource
    private DebugDataMapper debugDataMapper;

    public String applyEngineToken(EngineRequest request) {
        Engine engine = engineMapper.getEngineById(request.getEngineCode());
        if (request.getEngineSecret().equals(engine.getSecret())){
            return JwtUtils.createEngineToken(engine);
        }
        throw new EngineVerifyException("id或secret填写不正确");
    }

    public String engineHeartbeat(EngineRequest request) {
        Engine engine = engineMapper.getEngineById(request.getEngineCode());
        if(engine == null){
            throw new EngineVerifyException("引擎code不存在");
        }
        if (engine.getStatus().equals(EngineStatus.OFFLINE.toString())){
            engineMapper.updateStatus(request.getEngineCode(), EngineStatus.ONLINE.toString());
        }
        engineMapper.updateHeartbeat(request.getEngineCode(), System.currentTimeMillis());
        return "心跳成功";
    }

    public TaskResponse fetchEngineTask(EngineRequest request) {
        // 获取引擎任务
        TaskResponse response = new TaskResponse();
        Engine engine = engineMapper.getEngineById(request.getEngineCode());
        if(engine == null){
            throw new EngineVerifyException("引擎code不存在");
        }
        TaskDTO task;
        if(engine.getEngineType().equals(EngineType.SYSTEM.toString())){
            // 内置引擎调度优先调试后计划或集合
            task = taskMapper.getToRunTask(EngineType.SYSTEM.toString());
        }else {
            task = taskMapper.getToRunTask(engine.getId());
        }
        if(task == null){
            return null;
        }
        if(engine.getEngineType().equals(EngineType.SYSTEM.toString())) {
            // 更新 task执行引擎为当前获取
            int count = taskMapper.updateTaskEngine(request.getEngineCode(), task.getId());
            if (count == 0){    // 该条任务被别的引擎拿到 不再返回 等待下一次请求
                return null;
            }
        }
        // 获取本任务需要测试的用例列表
        List<TaskTestCollectionResponse> testCollectionList = caseJsonCreateService.getTaskTestCollectionList(task);
        try {
            // 组装测试数据 调试数据放在debugData中 计划或者集合数据生成json.zip 下载地址放在download中
            if (task.getSourceType().equals(ReportSourceType.TEMP.toString()) || task.getSourceType().equals(ReportSourceType.CASE.toString())) {
                JSONObject testCase = caseJsonCreateService.getDebugData(task, testCollectionList);
                response.setDownloadUrl(null);
                response.setDebugData(testCase);
            } else {
                // 计划和集合执行有下载测试数据地址 没有调试数据 测试数据打包放到下载地址下
                String downloadUrl = caseJsonCreateService.getDownloadUrl(task, testCollectionList);
                response.setDebugData(null);
                response.setDownloadUrl(downloadUrl);
            }
        }catch (Exception e){   // 出现错误直接给null
            response.setDownloadUrl(null);
            response.setDebugData(null);
        }
        response.setReRun(false);
        if(task.getSourceType().equals(ReportSourceType.PLAN.toString())){
            Plan plan = planMapper.getPlanDetail(task.getSourceId());
            if(plan.getRetry().equals("Y")){
                response.setReRun(true);
            }
        }
        response.setTaskId(task.getId());
        response.setTaskType(task.getType());
        response.setTestCollectionList(testCollectionList);
        // 更新任务及报告状态
        engineMapper.updateStatus(engine.getId(), EngineStatus.RUNNING.toString());
        taskMapper.updateTask(ReportStatus.RUNNING.toString(), task.getId());
        reportMapper.updateReportStatus(ReportStatus.RUNNING.toString(), task.getReportId());
        reportMapper.updateReportStartTime(task.getReportId(), System.currentTimeMillis(), System.currentTimeMillis());
        return response;
    }

    public String getTaskStatus(EngineRequest request){
        TaskDTO task = taskMapper.getTaskDetail(request.getTaskId());
        if(task.getStatus().equals(ReportStatus.DISCONTINUE.toString())){
            // 任务终止时 更新引擎状态为在线
            engineMapper.updateStatus(task.getEngineId(), EngineStatus.ONLINE.toString());
            return "STOP";
        }
        return null;
    }

    public void uploadCaseResult(EngineRequest request){
        TaskDTO task = taskMapper.getTaskDetail(request.getTaskId());
        List<CaseResultRequest> caseResultList = request.getCaseResultList();
        reportUpdateService.updateReport(task, caseResultList);
    }

    public void completeEngineTask(EngineRequest request){
        TaskDTO task = taskMapper.getTaskDetail(request.getTaskId());
        // 任务更新完成
        taskMapper.updateTask(ReportStatus.COMPLETED.toString(), task.getId());
        // 统计报告信息
        ReportStatistics reportStatistics = reportMapper.getReportStatistics(task.getReportId());
        String reportStatus;
        if(reportStatistics.getErrorCount() > 0){
            reportStatus = ReportStatus.ERROR.toString();
        }else if(reportStatistics.getFailCount() > 0){
            reportStatus = ReportStatus.FAIL.toString();
        }else if(reportStatistics.getPassCount() > 0){
            reportStatus = ReportStatus.SUCCESS.toString();
        }else {
            reportStatus = ReportStatus.SKIP.toString();
        }
        engineMapper.updateStatus(request.getEngineCode(), EngineStatus.ONLINE.toString());
        reportMapper.updateReportStatus(reportStatus, task.getReportId());
        reportMapper.updateReportEndTime(task.getReportId(), System.currentTimeMillis(), System.currentTimeMillis());
        // 删除任务文件 并通知执行人
        if(!task.getType().equals(TaskType.DEBUG.toString())){
            String taskZipPath = TASK_FILE_PATH+"/"+task.getProjectId()+"/"+task.getId()+".zip";
            FileUtils.deleteFile(taskZipPath);

            User user = userMapper.getUserInfo(task.getCreateUser());
            String title = "测试任务执行完成通知";
            String content = user.getUsername() + ", 您好!<br><br>您执行的任务: \""
                    + task.getName() + "\" 已执行完毕，请登录平台查看结果。<br><br>谢谢！";
            EmailUtils.sendMail(user.getEmail(), title, content, accessKey, accessSecret, runnerSenderAddress, runnerSenderName);
        }else {
            Report report = reportMapper.getReportDetail(task.getReportId());
            if (report.getSourceType().equals(ReportSourceType.TEMP.toString())){
                // 删除临时调试数据
                debugDataMapper.deleteDebugData(report.getSourceId());
            }
        }

    }

    public void uploadScreenshot(EngineRequest request) {
        try{
            UploadUtils.uploadImageB64(request.getFileName(), request.getBase64String(), uploadUrl, imageBucket, ak, sk);
        } catch (Exception exception) {
            throw new LMException("截图文件上传失败");
        }
    }

    public ResponseEntity<byte[]> downTestFile(String fileId) {
        TestFile testFile = testFileMapper.getTestFile(fileId);
        return FileUtils.downloadFile(testFile.getFilePath());
    }

    public ResponseEntity<byte[]> downTaskFile(String taskId) {
        TaskDTO task = taskMapper.getTaskDetail(taskId);
        String taskZipPath = TASK_FILE_PATH+"/"+task.getProjectId()+"/"+task.getId()+".zip";
        return FileUtils.downloadFile(taskZipPath);
    }

}
