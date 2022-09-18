package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class Device implements Serializable {
    private String id;

    private String serial;

    private String name;

    private String system;

    private String brand;

    private String model;

    private String version;

    private String size;

    private String sources;

    private String owner;

    private String user;

    private String agent;

    private Integer timeout;

    private Long createTime;

    private Long updateTime;

    private String status;

}