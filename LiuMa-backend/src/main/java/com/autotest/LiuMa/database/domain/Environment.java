package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Environment implements Serializable {
    private String id;

    private String name;

    private String projectId;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;

}