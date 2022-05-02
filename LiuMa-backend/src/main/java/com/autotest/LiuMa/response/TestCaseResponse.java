package com.autotest.LiuMa.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseResponse {

    @JSONField(ordinal = 1)
    private String comment;

    @JSONField(ordinal = 2)
    private String caseId;

    @JSONField(ordinal = 3)
    private String caseName;

    @JSONField(ordinal = 4)
    private JSONArray functions;

    @JSONField(ordinal = 5)
    private JSONObject params;

}
