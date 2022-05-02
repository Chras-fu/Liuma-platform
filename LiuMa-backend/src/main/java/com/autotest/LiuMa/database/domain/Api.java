package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Api implements Serializable {
    private String id;

    private Long num;

    private String name;

    private String level;

    private String moduleId;

    private String projectId;

    private String method;

    private String path;

    private String protocol;

    private String domainSign;

    private String description;

    private String header;

    private String body;

    private String query;

    private String rest;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private String status;

}