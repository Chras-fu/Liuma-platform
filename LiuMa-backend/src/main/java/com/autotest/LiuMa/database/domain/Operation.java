package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Operation implements Serializable {
    private String id;

    private String name;

    private String type;

    private String uiType;

    private String from;

    private String system;

    private String element;

    private String data;

    private String code;

    private String projectId;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;

}