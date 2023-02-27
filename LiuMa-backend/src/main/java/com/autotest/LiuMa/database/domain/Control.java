package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Control implements Serializable {
    private String id;

    private Long num;

    private String name;

    private String system;

    private String moduleId;

    private String projectId;

    private String by;

    private String expression;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private String status;

}