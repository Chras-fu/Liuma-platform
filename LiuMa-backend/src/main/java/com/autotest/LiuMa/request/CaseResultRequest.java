package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CaseResultRequest {
    private Integer status;

    private Long startTime;

    private Long endTime;

    private String collectionId;

    private String caseId;

    private String caseType;

    private String caseName;

    private String caseDesc;

    private Integer index;

    private Integer runTimes;

    private List<TransResultRequest> transactionList;
}
