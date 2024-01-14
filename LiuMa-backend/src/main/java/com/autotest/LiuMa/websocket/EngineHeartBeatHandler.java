package com.autotest.LiuMa.websocket;

import com.autotest.LiuMa.common.constants.EngineStatus;
import com.autotest.LiuMa.database.domain.Engine;
import com.autotest.LiuMa.database.mapper.EngineMapper;
import com.autotest.LiuMa.websocket.config.WsSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;

@Component
public class EngineHeartBeatHandler extends TextWebSocketHandler {

    @Resource
    private EngineMapper engineMapper;

    /**
     * socket 建立连接时
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String engineCode = (String) session.getAttributes().get("engineCode");
        Engine engine = engineMapper.getEngineById(engineCode);
        if (engine.getStatus().equals(EngineStatus.OFFLINE.toString())){
            engineMapper.updateStatus(engineCode, EngineStatus.ONLINE.toString());
        }
        engineMapper.updateHeartbeat(engineCode, System.currentTimeMillis());
        WsSessionManager.add("engine", engineCode, session);
    }

    /**
     * 接收消息事件
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 更新心跳
        String engineCode = (String) session.getAttributes().get("engineCode");
        engineMapper.updateHeartbeat(engineCode, System.currentTimeMillis());
    }

    /**
     * socket 断开连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String engineCode = (String) session.getAttributes().get("engineCode");
        if(engineCode == null){
            return;
        }
        Engine engine = engineMapper.getEngineById(engineCode);
        if (!engine.getStatus().equals(EngineStatus.OFFLINE.toString())){
            engineMapper.updateStatus(engineCode, EngineStatus.OFFLINE.toString());
        }
        WsSessionManager.remove("engine", engineCode);
    }
}