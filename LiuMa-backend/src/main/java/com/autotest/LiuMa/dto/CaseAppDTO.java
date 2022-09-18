package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.CaseApp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseAppDTO extends CaseApp {

    private String operationName;

    private String operationType;

}
