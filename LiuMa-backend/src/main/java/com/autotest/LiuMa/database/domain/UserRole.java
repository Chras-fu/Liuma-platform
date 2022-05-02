package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRole implements Serializable {
    private String id;

    private String userId;

    private String roleId;

    private Long createTime;

    private Long updateTime;

}