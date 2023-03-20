package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Domain implements Serializable {
    private String id;

    private String domainKeyType;

    private String domainKey;

    private String domainData;

    private String environmentId;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;
}