package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.CaseApi;
import com.autotest.LiuMa.database.domain.CaseWeb;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseWebDTO extends CaseWeb {

    private String operationName;

    private String operationType;

}
