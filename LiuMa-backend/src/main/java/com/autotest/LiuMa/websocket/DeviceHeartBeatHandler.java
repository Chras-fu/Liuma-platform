package com.autotest.LiuMa.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.DeviceStatus;
import com.autotest.LiuMa.database.domain.Device;
import com.autotest.LiuMa.database.mapper.DeviceMapper;
import com.autotest.LiuMa.websocket.config.WsSessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class DeviceHeartBeatHandler extends TextWebSocketHandler {

    @Resource
    private DeviceMapper deviceMapper;

    public void saveDevice(Device device) {
        Device oldDevice = deviceMapper.getDeviceBySerial(device.getSerial());
        if(oldDevice == null){ // 新增设备
            device.setId(UUID.randomUUID().toString());
            device.setCreateTime(System.currentTimeMillis());
            device.setUpdateTime(System.currentTimeMillis());
            deviceMapper.addDevice(device);
        }else{ // 修改设备
            device.setId(oldDevice.getId());
            device.setName(oldDevice.getName());    // 设备名字不再修改
            device.setUpdateTime(System.currentTimeMillis());
            deviceMapper.updateDevice(device);
        }
    }

    /**
     * socket 建立连接时
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String agentId = UUID.randomUUID().toString();
        session.getAttributes().put("agentId", agentId);
        WsSessionManager.add("agent", agentId, session);
        session.sendMessage(new TextMessage(agentId));
    }

    /**
     * 接收消息事件
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        Object agent = session.getAttributes().get("agentId");
        Object owner = session.getAttributes().get("owner");
        Object project = session.getAttributes().get("project");
        JSONObject msg = JSON.parseObject(payload);
        String command = msg.getString("command");
        if(command.equals("init")){
            Device device = new Device();
            device.setSerial(msg.getString("serial"));
            device.setName(msg.getJSONObject("properties").getString("name"));
            device.setSystem(msg.getJSONObject("properties").getString("system"));
            device.setBrand(msg.getJSONObject("properties").getString("brand"));
            device.setVersion(msg.getJSONObject("properties").getString("version"));
            device.setModel(msg.getJSONObject("properties").getString("model"));
            device.setSize(msg.getJSONObject("properties").getString("size"));
            device.setAgent(agent.toString());
            device.setOwner(owner.toString());
            device.setUser("");
            device.setTimeout(0);
            device.setProjectId(project.toString());
            device.setSources(JSONObject.toJSONString(msg.getJSONObject("agent")));
            device.setStatus(DeviceStatus.ONLINE.toString());
            this.saveDevice(device);
        }else { // delete
            Device device = deviceMapper.getDeviceBySerial(msg.getString("serial"));
            if(device==null){
                return;
            }
            device.setSources(JSONObject.toJSONString(new JSONObject()));
            device.setUser("");
            device.setTimeout(0);
            device.setStatus(DeviceStatus.OFFLINE.toString());
            device.setUpdateTime(System.currentTimeMillis());
            deviceMapper.updateDevice(device);
        }
    }

    /**
     * socket 断开连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Object agent = session.getAttributes().get("agentId");
        if (agent != null) {
            // 心跳关闭 更新设备状态和数据
            WsSessionManager.remove("agent", agent.toString());
            deviceMapper.updateDeviceByAgent(agent.toString());
        }
    }
}