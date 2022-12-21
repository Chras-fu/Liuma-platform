package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportCollectionCaseApi implements Serializable {
    private String id;

    private String reportCollectionCaseId;

    private Integer caseApiIndex;

    private String apiId;

    private String apiName;

    private String apiPath;

    private String description;

    private String execLog;

    private Integer during;

    private String status;

}