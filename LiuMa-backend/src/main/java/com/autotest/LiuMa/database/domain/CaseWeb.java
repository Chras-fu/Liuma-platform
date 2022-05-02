package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class CaseWeb implements Serializable {
    private String id;

    private Long index;

    private String caseId;

    private String operationId;

    private String element;

    private String data;

}