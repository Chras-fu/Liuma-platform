package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.TaskType;
import com.autotest.LiuMa.database.domain.Notification;
import com.autotest.LiuMa.database.domain.User;
import com.autotest.LiuMa.database.mapper.*;
import com.autotest.LiuMa.dto.ReportDTO;
import com.autotest.LiuMa.dto.TaskDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import cn.hutool.http.HttpUtil;

@Service
public class NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ReportMapper reportMapper;

    public void saveNotification(Notification notification){
        if(notification.getId() == null || notification.getId().equals("")){
            //新增通知
            notification.setId(UUID.randomUUID().toString());
            notification.setCreateTime(System.currentTimeMillis());
            notification.setUpdateTime(System.currentTimeMillis());
        }else{
            // 更新通知
            notification.setUpdateTime(System.currentTimeMillis());
        }
        notificationMapper.saveNotification(notification);
    }

    public void deleteNotification(String id){
        notificationMapper.deleteNotification(id);
    }

    public Notification getNotificationById(String id){
        return notificationMapper.getNotificationById(id);
    }

    public List<Notification> getNotificationList(String projectId, String condition) {
        if(condition != null && !condition.equals("")){
            condition = "%"+condition+"%";
        }
        return notificationMapper.getNotificationList(projectId, condition);
    }

    public void sendNotification(Notification notification, TaskDTO task){
        String taskType = null;
        if(task.getType().equals(TaskType.RUN.toString())){
            taskType = "手工执行";
        }else {
            taskType = "定时任务";
        }
        ReportDTO report = reportMapper.getReportDetail(task.getReportId());
        User user = userMapper.getUserInfo(task.getCreateUser());
        Long during = (report.getEndTime() - report.getStartTime()) / 1000;
        String params = notification.getParams().
                replace("{reportTitle}", report.getName()).
                replace("{taskType}", taskType).
                replace("{user}", user.getAccount()).
                replace("{caseNum}", report.getTotal().toString()).
                replace("{caseSuccess}", report.getPassCount().toString()).
                replace("{caseFail}", report.getFailCount().toString()).
                replace("{caseError}", report.getErrorCount().toString()).
                replace("{successPercent}", report.getPassRate()).
                replace("{executeTime}", during +"S");
        // 发送
        HttpUtil.post(notification.getWebhookUrl(), params);
    }

}
