package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void addNotificationData(Notification notification);
    void deleteNotificationData(String id);
    void updateNotificationData(Notification notification);
    Notification getNotificationData(@Param("projectId") String projectId, @Param("type") String type);

    //获取project的所有配置
    List<Notification> getAllProjectNotification(@Param("projectId") String projectId);

    //获取单条规则
    Notification getSpecificNotification(String notificationId);

}
