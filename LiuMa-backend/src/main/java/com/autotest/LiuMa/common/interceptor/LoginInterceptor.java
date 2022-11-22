package com.autotest.LiuMa.common.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.autotest.LiuMa.common.exception.TokenEmptyException;
import com.autotest.LiuMa.common.utils.JwtUtils;
import com.autotest.LiuMa.database.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     * 基于URL实现的拦截器
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        if (path.matches(Constant.LOGIN_PATH) || path.matches(Constant.ENGINE_TOKEN_PATH)
                || path.matches(Constant.REGISTER_PATH) || path.matches(Constant.SCREENSHOT_PATH)) {
            // 平台登录注册接口 引擎获取token接口 测试截图预览 不用拦截
            return true;
        } else if (path.matches(Constant.ENGINE_PATH)){
            // 引擎拦截验证
            String token = request.getHeader("token");
            if (token == null){
                throw new TokenEmptyException("token不能为空");
            }
            DecodedJWT jwt = JwtUtils.verifyToken(token);
            return true;
        } else {
            // 平台拦截验证
            String token = request.getHeader("token");
            if (token == null){
                throw new TokenEmptyException("token不能为空");
            }
            DecodedJWT jwt = JwtUtils.verifyToken(token);

            request.getSession(true).setAttribute("userId", jwt.getClaim("id").asString());
            request.getSession(true).setAttribute("account", jwt.getClaim("account").asString());

            Date lastDate = jwt.getIssuedAt(); // 获取签发时间
            if((new Date()).getTime() - lastDate.getTime() >= 10*60*1000){
                // 超过十分钟则刷新令牌 并放在header里返回
                User user = new User();
                user.setId(jwt.getClaim("id").asString());
                user.setUsername(jwt.getClaim("username").asString());
                user.setAccount(jwt.getClaim("account").asString());
                user.setPassword(jwt.getClaim("password").asString());
                String newToken = JwtUtils.createWebToken(user);
                response.addHeader("token", newToken);
            }else {
                response.addHeader("token", token);
            }
            return true;
        }
    }
}
