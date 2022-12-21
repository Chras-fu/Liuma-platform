package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportCollectionCaseApp implements Serializable {
    private String id;

    private String reportCollectionCaseId;

    private Integer caseAppIndex;

    private String operationId;

    private String operationName;

    private String operationElement;

    private String description;

    private String screenshot;

    private String execLog;

    private String status;

}