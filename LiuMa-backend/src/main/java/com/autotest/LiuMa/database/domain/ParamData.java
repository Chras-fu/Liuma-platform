package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ParamData implements Serializable {
    private String id;

    private String name;

    private String paramData;

    private String groupId;

    private String dataType;

    private String description;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private Integer status;

}