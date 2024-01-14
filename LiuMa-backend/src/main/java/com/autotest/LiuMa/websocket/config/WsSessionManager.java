package com.autotest.LiuMa.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class WsSessionManager {
    /**
     * 保存连接 session 的地方
     */
    private static ConcurrentHashMap<String, WebSocketSession> AGENT_SESSION_POOL = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, WebSocketSession> ENGINE_SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 添加 session
     */
    public static void add(String type, String key, WebSocketSession session) {
        if(type.equals("agent")) {
            AGENT_SESSION_POOL.put(key, session);
        }else {
            ENGINE_SESSION_POOL.put(key, session);
        }
    }

    /**
     * 删除 session,会返回删除的 session
     */
    public static void remove(String type, String key) {
        if(type.equals("agent")) {
            AGENT_SESSION_POOL.remove(key);
        }else {
            ENGINE_SESSION_POOL.remove(key);
        }
    }

    /**
     * 获得 session
     */
    public static WebSocketSession get(String type, String key) {
        if(type.equals("agent")) {
            return AGENT_SESSION_POOL.get(key);
        }else {
            return ENGINE_SESSION_POOL.get(key);
        }
    }
}