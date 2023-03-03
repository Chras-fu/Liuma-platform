package com.autotest.LiuMa.websocket.config;

import com.autotest.LiuMa.websocket.DeviceHeartBeatHandler;
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
    private WsInterceptor wsInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deviceHeartBeatHandler, "/websocket/heartbeat")
                .addInterceptors(wsInterceptor)
                .setAllowedOrigins("*"); // 解决跨域问题
    }
}
