package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Environment;
import com.autotest.LiuMa.dto.EnvironmentDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.EnvironmentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/environment")
public class EnvironmentController {

    @Resource
    private EnvironmentService environmentService;

    @PostMapping("/save")
    public void saveEnvironment(@RequestBody Environment environment, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        environment.setUpdateUser(user);
        environmentService.saveEnvironment(environment);
    }

    @PostMapping("/delete")
    public void deleteEnvironment(@RequestBody Environment environment) {
        environmentService.deleteEnvironment(environment);
    }

    @GetMapping("/all/{projectId}")
    public List<Environment> getAllEnvironment(@PathVariable String projectId) {
        return environmentService.getAllEnvironment(projectId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<EnvironmentDTO>> getEnvironmentList(@PathVariable int goPage, @PathVariable int pageSize,
                                                          @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, environmentService.getEnvironmentList(request.getProjectId(), request.getCondition()));
    }

}
