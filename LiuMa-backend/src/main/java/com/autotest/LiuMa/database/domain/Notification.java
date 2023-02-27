package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Notification implements Serializable {
    private String id;

    private String name;

    private String type;

    private String params;

    private String webhookUrl;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String status;

}
