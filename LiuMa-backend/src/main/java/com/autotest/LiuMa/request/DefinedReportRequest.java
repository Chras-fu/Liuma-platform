package com.autotest.LiuMa.request;

import lombok.Data;

@Data
public class DefinedReportRequest {
    private String reportTitle;
    private String taskDesc;
    private String taskType;
    private String user;
    private String caseNum;
    private String caseSucc;
    private String caseFail;
    private String succPercent;
    private String executeTime;
}
