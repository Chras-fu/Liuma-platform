package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Task implements Serializable {
    private String id;

    private String name;

    private String type;

    private String status;

    private String engineId;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

}