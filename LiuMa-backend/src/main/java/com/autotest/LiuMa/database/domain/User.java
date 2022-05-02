package com.autotest.LiuMa.database.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class User implements Serializable {
    private String id;

    private String username;

    private String account;

    private String password;

    private String status;

    private Long createTime;

    private Long updateTime;

    private Long mobile;

    private String lastProject;

    private String email;
}