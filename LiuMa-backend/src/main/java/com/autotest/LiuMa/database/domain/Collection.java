package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Collection implements Serializable {
    private String id;

    private String name;

    private String deviceId;

    private String versionId;

    private String description;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;

}