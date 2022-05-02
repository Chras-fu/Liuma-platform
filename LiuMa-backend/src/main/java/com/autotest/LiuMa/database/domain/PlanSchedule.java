package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlanSchedule implements Serializable {
    private String id;

    private String planId;

    private String startTime;

    private String frequency;

    private Long nextRunTime;

}