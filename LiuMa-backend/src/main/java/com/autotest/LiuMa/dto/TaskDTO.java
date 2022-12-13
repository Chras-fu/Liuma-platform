package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO extends Task {

    private String username;

    private String reportId;

    private String sourceType;

    private String sourceId;

    private String environmentId;

    private String deviceId;

}
