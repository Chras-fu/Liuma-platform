package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.CollectionCase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionCaseDTO extends CollectionCase {
    private String caseName;

    private String caseModule;

    private String caseType;

    private String caseSystem;
}
