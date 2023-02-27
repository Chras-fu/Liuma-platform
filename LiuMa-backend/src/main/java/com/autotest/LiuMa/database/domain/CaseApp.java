package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CaseApp implements Serializable {
    private String id;

    private Long index;

    private String caseId;

    private String operationId;

    private String description;

    private String element;

    private String data;

}