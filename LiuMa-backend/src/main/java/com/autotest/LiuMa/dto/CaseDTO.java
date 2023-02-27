package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.Case;
import com.autotest.LiuMa.database.domain.CaseApp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaseDTO extends Case {
    private String moduleName;

    private String username;

    private List<CaseApiDTO> caseApis;

    private List<CaseWebDTO> caseWebs;

    private List<CaseAppDTO> caseApps;
}
