package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Report;
import com.autotest.LiuMa.database.domain.ReportCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ReportCollectionDTO extends ReportCollection {
    private Integer passCount;

    private Integer failCount;

    private Integer errorCount;

    private List<ReportCollectionCaseDTO> caseList;

}
