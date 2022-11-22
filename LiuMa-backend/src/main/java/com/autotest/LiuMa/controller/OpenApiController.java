package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.request.EngineRequest;
import com.autotest.LiuMa.response.TaskResponse;
import com.autotest.LiuMa.service.OpenApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public ResponseEntity<byte[]> downloadTaskFile(@PathVariable String taskId) {
        return openApiService.downTaskFile(taskId);
    }

    @GetMapping("/test/file/download/{fileId}")
    public ResponseEntity<byte[]> downloadTestFile(@PathVariable String fileId) {
        return openApiService.downTestFile(fileId);
    }

    @GetMapping("/screenshot/{date}/{imageId}")
    public ResponseEntity<byte[]> previewImage(@PathVariable String date, @PathVariable String imageId) {
        return openApiService.previewImage(date, imageId);
    }
}
