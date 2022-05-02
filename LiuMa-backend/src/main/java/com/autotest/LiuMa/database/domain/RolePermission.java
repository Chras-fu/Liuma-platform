package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class RolePermission implements Serializable {
    private String id;

    private String roleId;

    private String permissionId;

    private Long createTime;

    private Long updateTime;

}