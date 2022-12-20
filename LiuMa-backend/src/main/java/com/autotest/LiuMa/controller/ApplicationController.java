package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Application;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ApplicationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/application")
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    @PostMapping("/save")
    public void saveApplication(@RequestBody Application application) {
        applicationService.saveApplication(application);
    }

    @PostMapping("/delete")
    public void deleteApplication(@RequestBody Application application) {
        applicationService.deleteApplication(application.getId());
    }

    @GetMapping("/list/{system}/{projectId}")
    public List<Application> getApplicationList(@PathVariable String system, @PathVariable String projectId) {
        return applicationService.getApplicationList(projectId, null, system);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Application>> getApplicationPageList(@PathVariable int goPage, @PathVariable int pageSize,
                                                      @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, applicationService.getApplicationList(request.getProjectId(), request.getCondition(), null));
    }
}
