package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.autotest.LiuMa.dto.ApiParamRuleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ApiParamRuleRequest {

    private String apiId;

    private List<ApiParamRuleDTO> header;

    private List<ApiParamRuleDTO>  body;

    private List<ApiParamRuleDTO>  query;

    private List<ApiParamRuleDTO>  rest;

    private JSONArray  positiveAssertion;   // 正向断言

    private JSONArray  oppositeAssertion;   // 反向断言

    private String createUser;
}
