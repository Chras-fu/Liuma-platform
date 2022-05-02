package com.autotest.LiuMa.database.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportStatistics implements Serializable {
    private String id;

    private String reportId;

    private Integer total;

    private Integer passCount;

    private Integer failCount;

    private Integer errorCount;

}