package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Project;
import com.autotest.LiuMa.database.domain.Role;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.domain.UserProject;
import com.autotest.LiuMa.dto.PlanDTO;
import com.autotest.LiuMa.dto.ProjectDTO;
import com.autotest.LiuMa.dto.RoleDTO;
import com.autotest.LiuMa.dto.UserDTO;
import com.autotest.LiuMa.request.ProjectUserRequest;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.ProjectService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/autotest/project")
public class ProjectController {

    @Resource
    private ProjectService projectService;

    @GetMapping("/user/{userId}")
    public List<Project> getUserProject(@PathVariable String userId) {
        return projectService.getUserProject(userId);
    }

    @GetMapping("/info")
    public Project getProjectInfo(@RequestParam String projectId) {
        return projectService.getProjectInfo(projectId);
    }

    @PostMapping("/add")
    public void saveProject(@RequestBody Project project){
        projectService.saveProject(project);
    }

    @PostMapping("/user/save")
    public void saveProjectUser(@RequestBody ProjectUserRequest request){
        projectService.saveProjectUser(request);
    }

    @PostMapping("/user/delete")
    public void deleteProjectUser(@RequestBody UserProject request){
        projectService.deleteProjectUser(request.getProjectId(), request.getUserId());
    }

    @PostMapping("/delete")
    public void deleteProject(@RequestBody Project project){
        projectService.deleteProject(project.getId());
    }

    @PostMapping("/recover")
    public void recoverProject(@RequestBody Project project){
        projectService.recoverProject(project.getId());
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<ProjectDTO>> getProjectList(@PathVariable int goPage, @PathVariable int pageSize,
                                                  @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, projectService.getProjectList(request));
    }

    @PostMapping("/user/list/{goPage}/{pageSize}")
    public Pager<List<User>> getProjectUserList(@PathVariable int goPage, @PathVariable int pageSize,
                                                @RequestBody QueryRequest queryRequest, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        queryRequest.setRequestUser(user);   // 设置查询人
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, projectService.getProjectUserList(queryRequest));
    }

    @GetMapping("/role/list")
    public List<Role> getProjectRoleList(@RequestParam String projectId) {
        return projectService.getProjectRoleList(projectId);
    }
}
