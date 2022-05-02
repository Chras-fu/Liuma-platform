package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Engine implements Serializable {
    private String id;

    private String name;

    private String engineType;

    private String secret;

    private String status;

    private Long lastHeartbeatTime;

    private String projectId;

    private String createUser;

    private String updateUser;

    private Long createTime;

    private Long updateTime;

}