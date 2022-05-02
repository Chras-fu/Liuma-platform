package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserProject implements Serializable {
    private String id;

    private String userId;

    private String projectId;

    private Long createTime;

    private Long updateTime;

}