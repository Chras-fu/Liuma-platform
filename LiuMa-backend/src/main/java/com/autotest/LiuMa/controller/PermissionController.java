package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.dto.MenuDTO;
import com.autotest.LiuMa.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @GetMapping("/autotest/menu/list")
    public List<MenuDTO> getMenus(@RequestParam(name="userId") String userId, @RequestParam(name = "projectId") String projectId) {
        return permissionService.getMenus(userId, projectId);
    }

    @GetMapping("/autotest/setting/permission")
    public Boolean getSettingOptionPermission(@RequestParam(name="userId") String userId, @RequestParam(name = "projectId") String projectId) {
        return permissionService.getSettingOptionPermission(userId, projectId);
    }
}
