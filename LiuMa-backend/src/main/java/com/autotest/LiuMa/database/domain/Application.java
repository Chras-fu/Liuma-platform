package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Application implements Serializable {
    private String id;

    private String name;

    private String system;

    private String appId;

    private String mainActivity;

    private String description;

    private String projectId;

    private Long createTime;

    private Long updateTime;

}