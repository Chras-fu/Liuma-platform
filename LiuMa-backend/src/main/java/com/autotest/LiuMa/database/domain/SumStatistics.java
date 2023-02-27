package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SumStatistics implements Serializable {
    private String id;

    private String projectId;

    private Integer apiCaseTotal=0;

    private Integer apiCaseNewWeek=0;

    private Integer webCaseTotal=0;

    private Integer webCaseNewWeek=0;

    private Integer appCaseTotal=0;

    private Integer appCaseNewWeek=0;

    private Integer caseRunTotal=0;

    private Integer caseRunToday=0;

    private String planRunWeekTop;

    private String caseFailWeekTop;

}