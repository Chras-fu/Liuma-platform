package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class DomainSign implements Serializable {
    private String id;

    private String name;

    private String description;

    private String projectId;

    private Long createTime;

    private Long updateTime;
}