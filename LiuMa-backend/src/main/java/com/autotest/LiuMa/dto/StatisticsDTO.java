package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsDTO {

    private String projectId;

    private String id;

    private String name;

    private Integer count;

    private Integer pass;

    private Float passRate;

}
