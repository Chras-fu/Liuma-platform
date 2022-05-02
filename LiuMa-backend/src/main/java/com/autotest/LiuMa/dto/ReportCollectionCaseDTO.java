package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Report;
import com.autotest.LiuMa.database.domain.ReportCollectionCase;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ReportCollectionCaseDTO extends ReportCollectionCase {

    private List<ReportCollectionCaseTransDTO> transList;

}
