package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Driver implements Serializable {
    private String id;

    private String name;

    private String setting;

    private String description;

    private String projectId;

    private Long createTime;

    private Long updateTime;

}