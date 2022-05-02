package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EngineRequest {
    private String engineCode;

    private String engineSecret;

    private String timestamp;

    private String taskId;

    private String fileName;

    private String base64String;

    private List<CaseResultRequest> caseResultList;
}
