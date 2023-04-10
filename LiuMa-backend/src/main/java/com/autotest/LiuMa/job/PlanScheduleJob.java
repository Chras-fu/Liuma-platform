package com.autotest.LiuMa.job;

import com.autotest.LiuMa.service.ScheduleJobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PlanScheduleJob {

    @Resource
    ScheduleJobService scheduleJobService;

    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟执行一次
    public void updateTimeoutTask(){
        scheduleJobService.runSchedulePlan();
    }
}
