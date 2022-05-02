package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectUserRequest {
    private Boolean isEdit;
    private String projectId;
    private List<String> userIds;
    private List<String> roleIds;
}
