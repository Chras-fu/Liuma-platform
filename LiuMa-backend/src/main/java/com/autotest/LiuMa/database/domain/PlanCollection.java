package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlanCollection implements Serializable {
    private String id;

    private String planId;

    private String collectionId;

}