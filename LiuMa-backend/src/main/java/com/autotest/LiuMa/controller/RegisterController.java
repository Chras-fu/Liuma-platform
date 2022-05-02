package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.request.RegisterRequest;
import com.autotest.LiuMa.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping
public class RegisterController {

    @Resource
    private UserService userService;

    @PostMapping("/autotest/register")
    public String registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

}
