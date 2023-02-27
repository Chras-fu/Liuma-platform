package com.autotest.LiuMa.response;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseAppResponse extends TestCaseResponse{

    @JSONField(ordinal = 6)
    private String appId;

    @JSONField(ordinal = 7)
    private String activity;

    @JSONField(ordinal = 8)
    private String deviceSystem;

    @JSONField(ordinal = 9)
    private String deviceUrl;

    @JSONField(ordinal = 10)
    private List<TestCaseAppDataResponse> optList; // APP用例的操作列表

}
