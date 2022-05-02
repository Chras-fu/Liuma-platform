package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class VerifyCode implements Serializable {
    private String id;

    private Long mobile;

    private String code;

    private Long createTime;

}