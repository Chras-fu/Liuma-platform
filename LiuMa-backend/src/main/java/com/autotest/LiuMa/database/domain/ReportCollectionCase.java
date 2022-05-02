package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportCollectionCase implements Serializable {
    private String id;

    private String reportCollectionId;

    private Integer collectionCaseIndex;

    private String caseId;

    private String caseType;

    private String caseName;

    private String caseDesc;

    private Integer runTimes;

    private Long startTime;

    private Long endTime;

    private String during;

    private String status;

}