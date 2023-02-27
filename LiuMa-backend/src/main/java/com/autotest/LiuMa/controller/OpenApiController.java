package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.dto.ReportDTO;
import com.autotest.LiuMa.request.EngineRequest;
import com.autotest.LiuMa.request.RunRequest;
import com.autotest.LiuMa.response.TaskResponse;
import com.autotest.LiuMa.service.OpenApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/openapi")
public class OpenApiController {

    @Resource
    private OpenApiService openApiService;

    @PostMapping("/engine/token/apply")
    public String applyEngineToken(@RequestBody EngineRequest request) {
        return openApiService.applyEngineToken(request);
    }

    @PostMapping("/engine/heartbeat/send")
    public String engineHeartbeat(@RequestBody EngineRequest request) {
        return openApiService.engineHeartbeat(request);
    }

    @PostMapping("/engine/task/fetch")
    public TaskResponse fetchEngineTask(@RequestBody EngineRequest request) {
        return openApiService.fetchEngineTask(request);
    }

    @PostMapping("/engine/task/status")
    public String getTaskStatus(@RequestBody EngineRequest request) {
        return openApiService.getTaskStatus(request);
    }

    @PostMapping("/engine/result/upload")
    public void uploadCaseResult(@RequestBody EngineRequest request) {
        openApiService.uploadCaseResult(request);
    }

    @PostMapping("/engine/task/complete")
    public void completeEngineTask(@RequestBody EngineRequest request) {
        openApiService.completeEngineTask(request);
    }

    @PostMapping("/engine/screenshot/upload")
    public void uploadScreenshot(@RequestBody EngineRequest request) {
        openApiService.uploadScreenshot(request);
    }

    @GetMapping("/task/file/download/{taskId}")
    public void downloadTaskFile(@PathVariable String taskId, HttpServletResponse response) {
        openApiService.downTaskFile(taskId, response);
    }

    @GetMapping("/download/test/file/{fileId}")
    public void downloadTestFile(@PathVariable String fileId, HttpServletResponse response) {
        openApiService.downloadTestFile(fileId, response);
    }

    @GetMapping("/download/package/{date}/{fileId}/{packageName}")
    public void downloadAppPackage(@PathVariable String date, @PathVariable String fileId,  @PathVariable String packageName, HttpServletResponse response) {
        openApiService.downloadAppPackage(date, fileId, packageName, response);
    }

    @GetMapping("/screenshot/{date}/{imageId}")
    public ResponseEntity<byte[]> previewImage(@PathVariable String date, @PathVariable String imageId) {
        return openApiService.previewImage(date, imageId);
    }

    @PostMapping("/exec/test/plan")
    public String execTestPlan(@RequestBody RunRequest request) {
        return openApiService.execTestPlan(request);
    }

    @PostMapping("/exec/result/{taskId}")
    public ReportDTO getPlanReport(@PathVariable String taskId) {
        return openApiService.getPlanReport(taskId);
    }
}
