package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Engine;
import com.autotest.LiuMa.database.domain.Task;
import com.autotest.LiuMa.dto.EngineDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.EngineService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/engine")
public class EngineController {

    @Resource
    private EngineService engineService;

    @PostMapping("/register")
    public Engine saveEngine(@RequestBody Engine engine, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        engine.setUpdateUser(user);
        return engineService.saveEngine(engine);
    }

    @PostMapping("/delete")
    public void deleteEngine(@RequestBody Engine engine) {
        engineService.deleteEngine(engine);
    }

    @PostMapping("/stop/task")
    public void stopEngineTask(@RequestBody Task task) {
        engineService.stopEngineTask(task);
    }

    @PostMapping("/stop/all/task")
    public void stopEngineAllTask(@RequestBody Engine engine) {
        engineService.stopEngineAllTask(engine.getId());
    }

    @GetMapping("/all/{projectId}")
    public List<Engine> getAllCustomEngine(@PathVariable String projectId) {
        return engineService.getAllCustomEngine(projectId);
    }

    @GetMapping("/detail/{engineId}")
    public EngineDTO getEngineDetail(@PathVariable String engineId) {
        return engineService.getEngineById(engineId);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<EngineDTO>> getEnvironmentList(@PathVariable int goPage, @PathVariable int pageSize,
                                                     @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, engineService.getEngineList(request.getProjectId(), request.getCondition()));
    }

}
