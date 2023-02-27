package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class DailyStatistics implements Serializable {
    private String id;

    private String statDate;

    private String projectId;

    private Integer apiCaseNew=0;

    private Integer webCaseNew=0;

    private Integer appCaseNew=0;

    private Integer apiCaseSum=0;

    private Integer webCaseSum=0;

    private Integer appCaseSum=0;

    private Integer apiCaseRun=0;

    private Integer webCaseRun=0;

    private Integer appCaseRun=0;

    private Float apiCasePassRate=0.0F;

    private Float webCasePassRate=0.0F;

    private Float appCasePassRate=0.0F;

}