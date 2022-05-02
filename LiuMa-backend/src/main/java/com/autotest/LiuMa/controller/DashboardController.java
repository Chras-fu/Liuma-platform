package com.autotest.LiuMa.controller;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/autotest/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/get/{projectId}")
    public JSONObject getDashboardData(@PathVariable String projectId) {
        return dashboardService.getDashboardData(projectId);
    }

}
