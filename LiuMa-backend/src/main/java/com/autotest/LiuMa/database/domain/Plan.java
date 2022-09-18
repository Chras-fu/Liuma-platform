package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Plan implements Serializable {
    private String id;

    private String name;

    private String versionId;

    private String description;

    private String environmentId;

    private Integer maxThread;

    private String retry;

    private String engineId;

    private String projectId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

}