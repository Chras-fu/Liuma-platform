package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.database.domain.Device;
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

    @PostMapping("/stop/{serial}")
    public void stopUseDevice(@PathVariable String serial) {
        deviceService.stopUseDevice(serial);
    }

    @PostMapping("/active/{serial}")
    public Boolean activeDevice(@PathVariable String serial, HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.activeDevice(serial, user);
    }

    @PostMapping("/update")
    public void updateDevice(@RequestBody Device device) {
        deviceService.updateDevice(device);
    }

    @PostMapping("/use/{serial}/{timeout}")
    public Boolean UseDevice(@PathVariable String serial, @PathVariable Integer timeout,  HttpServletRequest request) {
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.useDevice(serial, timeout, user);
    }

    @GetMapping("/detail/{serial}")
    public Device getDeviceDetail(@PathVariable String serial) {
        return deviceService.getDeviceDetail(serial);
    }

    @PostMapping("/list")
    public List<Device> getDeviceList( @RequestBody QueryRequest queryRequest,  HttpServletRequest request){
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.getDeviceList(queryRequest, user);
    }

    @GetMapping("/{system}/list/{projectId}")
    public List<Device> getDeviceListBySystem(@PathVariable String system, @PathVariable String projectId, HttpServletRequest request){
        String user = request.getSession().getAttribute("userId").toString();
        return deviceService.getDeviceListBySystem(projectId, system, user);
    }

}
