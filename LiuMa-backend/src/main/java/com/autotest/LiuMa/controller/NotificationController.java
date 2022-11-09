package com.autotest.LiuMa.controller;

import com.autotest.LiuMa.common.utils.PageUtils;
import com.autotest.LiuMa.common.utils.Pager;
import com.autotest.LiuMa.database.domain.Notification;
import com.autotest.LiuMa.request.QueryRequest;
import com.autotest.LiuMa.service.NotificationService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/autotest/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @PostMapping("/save")
    public void saveNotification(@RequestBody Notification notificationRequest){
        notificationService.saveNotification(notificationRequest);
    }

    @PostMapping("/delete")
    public void deleteNotification(@RequestBody Notification notification){
        notificationService.deleteNotification(notification.getId());
    }

    @GetMapping("/list/{projectId}")
    public List<Notification> getNotificationList(@PathVariable String projectId) {
        return notificationService.getNotificationList(projectId, null);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<Notification>> getNotificationPageList(@PathVariable int goPage, @PathVariable int pageSize,
                                                   @RequestBody QueryRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, notificationService.getNotificationList(request.getProjectId(), request.getCondition()));
    }

}
