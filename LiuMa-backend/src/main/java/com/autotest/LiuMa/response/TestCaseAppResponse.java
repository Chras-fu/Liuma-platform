package com.autotest.LiuMa.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseAppResponse extends TestCaseResponse{

    @JSONField(ordinal = 6)
    private Boolean startDriver;

    @JSONField(ordinal = 7)
    private Boolean closeDriver;

    @JSONField(ordinal = 8)
    private List<TestCaseAppDataResponse> optList; // WEB用例的操作列表

}
