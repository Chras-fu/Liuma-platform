package com.autotest.LiuMa.job;

import com.autotest.LiuMa.service.ScheduleJobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TaskScheduleJob {

    @Resource
    ScheduleJobService scheduleJobService;

    @Scheduled(cron = "0 0/3 * * * ?") // 三分钟执行一次
    public void updateTimeoutTask(){
        scheduleJobService.updateTimeoutTask();
    }
}
