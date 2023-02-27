package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Case implements Serializable {
    private String id;

    private Long num;

    private String name;

    private String level;

    private String moduleId;

    private String projectId;

    private String type;

    private String thirdParty;

    private String description;

    private String environmentIds;

    private String system;

    private String commonParam;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private String status;

}