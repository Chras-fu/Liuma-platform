package com.autotest.LiuMa.response;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseWebResponse extends TestCaseResponse{

    @JSONField(ordinal = 6)
    private Boolean startDriver;

    @JSONField(ordinal = 7)
    private Boolean closeDriver;

    @JSONField(ordinal = 8)
    private JSONObject driverSetting;

    @JSONField(ordinal = 9)
    private List<TestCaseWebDataResponse> optList; // WEB用例的操作列表

}
