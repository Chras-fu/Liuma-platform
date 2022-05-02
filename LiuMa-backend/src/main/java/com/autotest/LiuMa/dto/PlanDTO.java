package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Plan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanDTO extends Plan {

    private String startTime;

    private String frequency;

    private String username;

    private String versionName;

    private String environmentName;

    private List<PlanCollectionDTO> planCollections;

}
