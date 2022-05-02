package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Task;
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
    public Task run(@RequestBody RunRequest runRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        runRequest.setRunUser(user);
        return runService.run(runRequest);
    }
}
