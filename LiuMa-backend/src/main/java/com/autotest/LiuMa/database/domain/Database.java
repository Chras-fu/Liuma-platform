package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Database implements Serializable {
    private String id;

    private String databaseType;

    private String databaseKey;

    private String connectInfo;

    private String environmentId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;
}