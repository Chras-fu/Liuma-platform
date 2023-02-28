package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.service.ApiImportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/autotest/import")
public class ApiImportController {
    @Resource
    public ApiImportService apiImportService;

    @PostMapping(value="/api", consumes = {"multipart/form-data"})
    public void importApi( @RequestParam MultipartFile file, @RequestParam String
            sourceType, @RequestParam String projectId, @RequestParam String moduleId, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        apiImportService.importApi(file, sourceType, projectId, moduleId, userId);
    }

}
