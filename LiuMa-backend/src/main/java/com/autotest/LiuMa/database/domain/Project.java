package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Project implements Serializable {
    private String id;

    private String name;

    private String description;

    private String projectAdmin;

    private String status;

    private Long createTime;

    private Long updateTime;
}