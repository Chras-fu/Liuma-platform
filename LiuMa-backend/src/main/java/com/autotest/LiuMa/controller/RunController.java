package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.dto.TaskDTO;
import com.autotest.LiuMa.request.RunRequest;
import com.autotest.LiuMa.service.RunService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("")
public class RunController {

    @Resource
    private RunService runService;

    @PostMapping("/autotest/run")
    public TaskDTO run(@RequestBody RunRequest runRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        runRequest.setRunUser(user);
        return runService.run(runRequest);
    }
}
