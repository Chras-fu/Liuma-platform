package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Device;
import com.autotest.LiuMa.dto.DeviceDTO;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/autotest/device")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @GetMapping("/filter/{projectId}")
    public HashMap<String, List<String>> getDeviceFilter(@PathVariable String projectId, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.getDeviceFilter(user, projectId);
    }

    @PostMapping("/stop/{deviceId}")
    public void stopUseDevice(@PathVariable String deviceId) {
        deviceService.stopUseDevice(deviceId);
    }

    @PostMapping("/active/{deviceId}")
    public Boolean activeDevice(@PathVariable String deviceId, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.activeDevice(deviceId, user);
    }

    @PostMapping("/update")
    public void updateDevice(@RequestBody Device device) {
        deviceService.updateDevice(device);
    }

    @PostMapping("/use/{deviceId}/{timeout}")
    public Boolean UseDevice(@PathVariable String deviceId, @PathVariable Integer timeout,  HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.useDevice(deviceId, timeout, user);
    }

    @GetMapping("/detail/{deviceId}")
    public Device getDeviceDetail(@PathVariable String deviceId) {
        return deviceService.getDeviceDetail(deviceId);
    }

    @PostMapping("/list")
    public List<DeviceDTO> getDeviceList(@RequestBody QueryRequest queryRequest, HttpServletRequest request){
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.getDeviceList(queryRequest, user);
    }

    @GetMapping("/{system}/list/{projectId}")
    public List<Device> getDeviceListBySystem(@PathVariable String system, @PathVariable String projectId, HttpServletRequest request){
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.getDeviceListBySystem(projectId, system, user);
    }

}
