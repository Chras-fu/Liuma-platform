package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Project;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.dto.UserDTO;
import com.autotest.LiuMa.request.PasswordRequest;
import com.autotest.LiuMa.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/autotest/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/info/{id}")
    public User getUserInfo(@PathVariable String id) {
        return userService.getUserInfo(id);
    }

    @PostMapping("/switch/project")
    public UserDTO switchProject(@RequestBody User user) {
        return userService.switchProject(user.getId(), user.getLastProject());
    }

    @PostMapping("/update/password")
    public void updatePassword(@RequestBody PasswordRequest request) {
        userService.updatePassword(request);
    }

    @PostMapping("/update/info")
    public void updateInfo(@RequestBody User user) {
        userService.updateInfo(user);
    }

    @GetMapping("/all")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }

    @GetMapping("/role/list")
    public List<String> getUserRoleList(@RequestParam String projectId, @RequestParam String userId) {
        return userService.getUserRoleList(projectId, userId);
    }

    @GetMapping("/query")
    public List<User> queryUser(@RequestParam String account) {
        return userService.queryUser(account);
    }
}
