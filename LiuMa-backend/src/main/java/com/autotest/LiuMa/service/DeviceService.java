package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.common.constants.DeviceStatus;
import com.autotest.LiuMa.database.domain.Device;
import com.autotest.LiuMa.database.mapper.DeviceMapper;
import com.autotest.LiuMa.dto.DeviceDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.websocket.config.WsSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    public Device getDeviceDetail(String deviceId){
        return deviceMapper.getDeviceById(deviceId);
    }

    public void stopUseDevice(String deviceId){
        Device device = deviceMapper.getDeviceById(deviceId);
        this.coldDevice(device);
    }

    public void coldDevice(Device device) {
        device.setStatus(DeviceStatus.COLDING.toString());
        device.setUpdateTime(System.currentTimeMillis());
        device.setSources("{}");
        device.setUser("");
        device.setTimeout(0);
        deviceMapper.updateDevice(device);
        // 调用ws通知客户端冷却设备
        try {
            WebSocketSession session = WsSessionManager.get(device.getAgent());
            session.sendMessage(new TextMessage("cold@"+device.getSerial()));
        }catch (Exception ignored){

        }
    }

    public Boolean activeDevice(String deviceId, String user) {
        Device device = deviceMapper.getDeviceById(deviceId);
        if(user.equals(device.getUser()) && device.getStatus().equals(DeviceStatus.USING.toString())){
            device.setUpdateTime(System.currentTimeMillis());
            deviceMapper.updateDevice(device);
            return true;
        }
        return false;
    }

    public void updateDevice(Device device) {
        device.setUpdateTime(System.currentTimeMillis());
        deviceMapper.updateDevice(device);
    }

    public Boolean useDevice(String deviceId, Integer timeout, String user) {
        Device device = deviceMapper.getDeviceById(deviceId);
        if(!device.getStatus().equals(DeviceStatus.ONLINE.toString())){
            return false;   // 设备非空闲状态无法使用
        }
        device.setStatus(DeviceStatus.USING.toString());
        device.setUpdateTime(System.currentTimeMillis());
        device.setUser(user);
        device.setTimeout(timeout);
        deviceMapper.updateDevice(device);
        return true;
    }

    public List<DeviceDTO> getDeviceList(QueryRequest request, String owner) {
        if(request.getCondition() != null && !request.getCondition().equals("")){
            request.setCondition("%"+request.getCondition()+"%");
        }
        JSONObject filter = request.getFilter();
        List<String> brand = null;
        List<String> android = null;
        List<String> apple = null;
        List<String> size = null;
        if(filter != null) {
            brand = filter.getJSONArray("brand").toJavaList(String.class);
            android = filter.getJSONArray("android").toJavaList(String.class);
            apple = filter.getJSONArray("apple").toJavaList(String.class);
            size = filter.getJSONArray("size").toJavaList(String.class);
        }
        return deviceMapper.getDeviceList(request.getProjectId(), owner, request.getCondition(), request.getStatus(), brand, android, apple, size);
    }

    public List<Device> getDeviceListBySystem(String projectId, String system, String owner){
        return deviceMapper.getDeviceListBySystem(projectId, owner, system);
    }

    public HashMap<String, List<String>> getDeviceFilter(String owner, String projectId){
        HashMap<String, List<String>> filter = new HashMap<>();
        filter.put("brand", deviceMapper.getDeviceFilter(projectId, owner, "brand", null));
        filter.put("android", deviceMapper.getDeviceFilter(projectId, owner, "version", "android"));
        filter.put("apple", deviceMapper.getDeviceFilter(projectId, owner, "version", "apple"));
        filter.put("size", deviceMapper.getDeviceFilter(projectId, owner, "size", null));
        return filter;
    }

}
