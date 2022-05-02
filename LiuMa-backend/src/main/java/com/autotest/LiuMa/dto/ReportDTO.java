package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Report;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ReportDTO extends Report {

    private String username;    // 执行人名字

    private Long total;

    private Long passCount;

    private Long failCount;

    private Long errorCount;

    private String passRate;

    private Integer progress;

    private List<ReportCollectionDTO> collectionList;

}
