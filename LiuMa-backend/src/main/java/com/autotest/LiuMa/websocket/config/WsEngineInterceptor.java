package com.autotest.LiuMa.websocket.config;

import com.autotest.LiuMa.common.exception.EngineVerifyException;
import com.autotest.LiuMa.database.mapper.EngineMapper;
import com.autotest.LiuMa.dto.EngineDTO;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class WsEngineInterceptor implements HandshakeInterceptor {

    @Resource
    private EngineMapper engineMapper;

    /**
     * 握手前
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
        String engineCode = req.getServletRequest().getParameter("engineCode");
        String engineSecret = req.getServletRequest().getParameter("engineSecret");
        EngineDTO engineDTO =  engineMapper.getEngineById(engineCode);
        if(!engineSecret.equals(engineDTO.getSecret())){
            throw new EngineVerifyException("code或secret填写不正确");
        }
        attributes.put("engineCode", engineCode);
        attributes.put("engineSecret", engineSecret);
        return true;
    }

    /**
     * 握手后
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}