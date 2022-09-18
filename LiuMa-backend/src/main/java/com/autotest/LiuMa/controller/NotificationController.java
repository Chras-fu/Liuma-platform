package com.autotest.LiuMa.controller;

import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.database.domain.Notification;
import com.autotest.LiuMa.dto.NotificationDTO;
import com.autotest.LiuMa.request.DefinedReportRequest;
import com.autotest.LiuMa.request.NotificationRequest;
import com.autotest.LiuMa.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/autotest/notice")
public class NotificationController {
    @Resource
    private NotificationService notificationService;

    @PostMapping("/save")  //add or update
    public void saveNotificationData(@RequestBody NotificationRequest notificationRequest, HttpServletRequest request){
        String user = request.getSession().getAttribute("userId").toString();
        notificationRequest.setUserId(user);
        notificationService.saveNotification(notificationRequest);
    }
    @DeleteMapping("/delete")
    public void deleteNotificationData(String id){
        notificationService.deleteNotification(id);
    }

    @GetMapping("/query")
    public NotificationDTO queryNotificationData(@RequestParam String projectId, @RequestParam String type){
        NotificationDTO notificationDTO = new NotificationDTO();
        Notification queryNotification = notificationService.queryNotification(projectId, type);
        BeanUtils.copyProperties(queryNotification, notificationDTO);
        return notificationDTO;
    }
    //根据projectId查询当前项目所有的配置信息(便于前端查询数据)
    @GetMapping("/query/all/notification")
    public List queryProjectData(@RequestParam String projectId){
        return notificationService.getAllProjectNotification(projectId);
    }

    @PostMapping("/run")
    public String noticePlatform(@RequestParam String notificationId, @RequestBody DefinedReportRequest definedReportRequest){
        System.out.println("definedReportRequest: " + definedReportRequest);
        Notification notification = notificationService.querySpecificNotification(notificationId);
        if (notification.getStatus().equals("0")){
            return "The notification setting is closed";
        }
        String platform = notification.getType();
        String paramObject = notification.getParams();  //配置字段对象
        String webhookUrl = notification.getWebhookUrl();

        //将钉钉，微信，企业微信的单独封装起来
         notificationService.noticeFactory(platform, webhookUrl, paramObject, definedReportRequest);
         return platform + " send success";
    }

}
