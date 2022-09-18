package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Report implements Serializable {
    private String id;

    private String name;

    private String taskId;

    private String environmentId;

    private String deviceId;

    private String sourceType;

    private String sourceId;

    private Long startTime;

    private Long endTime;

    private String status;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

}