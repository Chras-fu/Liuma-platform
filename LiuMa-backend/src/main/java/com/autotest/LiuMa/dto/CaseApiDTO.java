package com.autotest.LiuMa.dto;

import com.autotest.LiuMa.database.domain.CaseApi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CaseApiDTO extends CaseApi {
    private String apiName;

    private String apiPath;

    private String apiMethod;

    private String apiProtocol;

    private String apiDomainSign;
}
