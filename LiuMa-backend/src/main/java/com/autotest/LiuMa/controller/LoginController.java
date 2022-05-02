package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.exception.LoginVerifyException;
import com.autotest.LiuMa.common.utils.JwtUtils;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.dto.UserDTO;
import com.autotest.LiuMa.request.LoginRequest;
import com.autotest.LiuMa.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;


@RestController
@RequestMapping
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/autotest/login")
    public UserDTO login(@RequestBody LoginRequest request, HttpServletResponse response) {
        UserDTO user = userService.getUser(request.getAccount());
        if(user != null) {
            if (user.getPassword().equals(request.getPassword())) {
                response.addHeader("token", JwtUtils.createWebToken(user));
                user.setPassword(null);
                user.setPermissions(userService.getUserProjectPermission(user.getId(), user.getLastProject()));
                return user;
            } else {
                throw new LoginVerifyException("账户密码校验失败");
            }
        }else{
            throw new LoginVerifyException("用户不存在");
        }
    }
}
