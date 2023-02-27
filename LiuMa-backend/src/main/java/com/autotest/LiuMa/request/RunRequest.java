package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RunRequest {
    private String engineId;
    private String environmentId;
    private String deviceId;
    private String sourceType;
    private String sourceId;
    private String sourceName;
    private String taskType;
    private String runUser;
    private String projectId;

    private CaseRequest debugData;

    private String user;
    private String planId;
}
