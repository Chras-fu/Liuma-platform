package com.autotest.LiuMa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.autotest.LiuMa.database.domain.Notification;
import com.autotest.LiuMa.database.mapper.NotificationMapper;
import com.autotest.LiuMa.request.DefinedReportRequest;
import com.autotest.LiuMa.request.NotificationRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import cn.hutool.http.HttpUtil;

@Service
public class NotificationService {
    @Resource
    private NotificationMapper notificationMapper;

    public void saveNotification(NotificationRequest notificationRequest){
        JSONObject notificationDataObject = (JSONObject) JSON.toJSON(notificationRequest);
        Notification notification = notificationDataObject.toJavaObject(Notification.class);
        Object tmp = notification.getId();
        if (notification.getId()==null ||notification.getId().equals("") ){
            notification.setId(UUID.randomUUID().toString());
            notification.setProjectId(notificationRequest.getProjectId());
            notification.setType(notificationRequest.getType());
            notification.setName(notificationRequest.getName());
            notification.setStatus(notificationRequest.getStatus());
            notification.setParams(notificationRequest.getParams() + "");
            notification.setCreateUser(notificationRequest.getUserId());
            notification.setUpdateUser(notificationRequest.getUserId());
            notification.setCreateTime(System.currentTimeMillis());
            notification.setUpdateTime(System.currentTimeMillis());
            notificationMapper.addNotificationData(notification);
        }else{ //修改接口
            notification.setProjectId(notificationRequest.getProjectId());
            notification.setType(notificationRequest.getType());
            notification.setName(notificationRequest.getName());
            notification.setStatus(notificationRequest.getStatus());
            notification.setParams(notificationRequest.getParams() + "");
            notification.setUpdateUser(notificationRequest.getUserId());
            notification.setUpdateTime(System.currentTimeMillis());
            notificationMapper.updateNotificationData(notification);
        }
    }

    public void deleteNotification(String id){
        notificationMapper.deleteNotificationData(id);
    }


    public Notification queryNotification(String projectId, String type){
        return notificationMapper.getNotificationData(projectId, type);
    }

    public List<Notification> getAllProjectNotification(String projectId){
        return notificationMapper.getAllProjectNotification(projectId);
    }

    //获取单条数据
    public Notification querySpecificNotification(String notificationId){
        return notificationMapper.getSpecificNotification(notificationId);
    }

    //将对平台的请求封装
    public void noticeFactory(String platfrom, String webhookUrl, String paramObject, DefinedReportRequest definedReportRequest){
        switch (platfrom){
            case "Dingding":
                System.out.println("Dingding: case");
                noticeDingding(webhookUrl,  paramObject, definedReportRequest);
                break;
            case "Feishu":
                System.out.println("Feishu: case");
                noticeFeishu(webhookUrl,paramObject, definedReportRequest);
                break;
            case "Wechat":
                System.out.println("Wechat: case");
                noticeWechat(webhookUrl, paramObject, definedReportRequest);
                break;
        }
    }

    //钉钉
    private void noticeDingding(String webhookUrl, String paramObject, DefinedReportRequest definedReportRequest) {
        String actualConfigData = replaceConfig(paramObject, definedReportRequest);
        System.out.println("dingding actualConfigData: " + actualConfigData);
        String dingdingResult = HttpUtil.post(webhookUrl, actualConfigData);
        System.out.println("dingding result: " + dingdingResult);
        System.out.println("dingding send data already");;
    }

    //飞书
    private void noticeFeishu(String webhookUrl, String paramObject, DefinedReportRequest definedReportRequest) {
        String actualConfigData = replaceConfig(paramObject, definedReportRequest);
        System.out.println("feishu actualConfigData: " + actualConfigData);
        String feishuResult = HttpUtil.post(webhookUrl, actualConfigData);
        System.out.println("feishu result: " + feishuResult);
        System.out.println("feishu send data already");
    }

    //企业微信
    private void noticeWechat(String webhookUrl, String paramObject, DefinedReportRequest definedReportRequest) {
        String actualConfigData = replaceConfig(paramObject, definedReportRequest);
        System.out.println("Wechat actualConfigData: " + actualConfigData);
        String feishuResult = HttpUtil.post(webhookUrl, actualConfigData);
        System.out.println("Wechat result: " + feishuResult);
        System.out.println("Wechat send data already");
    }
    //替换指定数据的封装
    private String replaceConfig(String paramObject, DefinedReportRequest definedReportRequest){
        return paramObject.
                replace("{reportTitle}",definedReportRequest.getReportTitle() ).
                replace("{taskDesc}",definedReportRequest.getTaskDesc() ).
                replace("{taskType}", definedReportRequest.getTaskType()).
                replace("{user}", definedReportRequest.getUser()).
                replace("{caseNum}", definedReportRequest.getCaseNum()).
                replace("{caseSucc}", definedReportRequest.getCaseSucc()).
                replace("{caseFail}", definedReportRequest.getCaseFail()).
                replace("{succPercent}", definedReportRequest.getSuccPercent()).
                replace("{executeTime}", definedReportRequest.getExecuteTime());
    }

}
