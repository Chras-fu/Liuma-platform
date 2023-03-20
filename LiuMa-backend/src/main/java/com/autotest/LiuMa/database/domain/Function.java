package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Function implements Serializable {
    private String id;

    private String name;

    private String from;

    private String param;

    private String code;

    private String expression;

    private String projectId;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;

}