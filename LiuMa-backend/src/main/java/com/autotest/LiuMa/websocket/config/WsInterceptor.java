package com.autotest.LiuMa.websocket.config;

import com.autotest.LiuMa.database.domain.Project;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.mapper.ProjectMapper;
import com.autotest.LiuMa.database.mapper.UserMapper;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class WsInterceptor implements HandshakeInterceptor {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ProjectMapper projectMapper;

    /**
     * 握手前
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
        String owner = req.getServletRequest().getParameter("owner");
        String project = req.getServletRequest().getParameter("project");
        if(owner != null && !owner.equals("system")) {
            User user = userMapper.getUser(owner);
            if (user == null) {
                return false;
            } else {
                owner = user.getId();
            }
        }else {
            owner = "system";
        }
        if(project != null && !project.equals("system")) {
            Project project1 = projectMapper.getProjectByName(project);
            if (project1 == null) {
                return false;
            } else {
                project = project1.getId();
            }
        }else {
            project = "system";
        }
        attributes.put("owner", owner);
        attributes.put("project", project);
        return true;
    }

    /**
     * 握手后
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}