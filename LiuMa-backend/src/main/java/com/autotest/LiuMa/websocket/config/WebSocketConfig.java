package com.autotest.LiuMa.websocket.config;

import com.autotest.LiuMa.websocket.DeviceHeartBeatHandler;
import com.autotest.LiuMa.websocket.EngineHeartBeatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DeviceHeartBeatHandler deviceHeartBeatHandler;

    @Autowired
    private EngineHeartBeatHandler engineHeartBeatHandler;

    @Autowired
    private WsAgentInterceptor wsAgentInterceptor;

    @Autowired
    private WsEngineInterceptor wsEngineInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deviceHeartBeatHandler, "/websocket/agent/heartbeat")
                .addInterceptors(wsAgentInterceptor)
                .setAllowedOrigins("*"); // 解决跨域问题
        registry.addHandler(engineHeartBeatHandler, "/websocket/engine/heartbeat")
                .addInterceptors(wsEngineInterceptor)
                .setAllowedOrigins("*"); // 解决跨域问题
    }
}
