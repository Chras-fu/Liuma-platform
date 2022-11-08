package com.autotest.LiuMa.database.mapper;

import com.autotest.LiuMa.database.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    void saveNotification(Notification notification);

    void deleteNotification(String id);

    void updateOtherNotificationStatus(String status, String id, String projectId);

    List<Notification> getNotificationList(String projectId, String condition);

}
