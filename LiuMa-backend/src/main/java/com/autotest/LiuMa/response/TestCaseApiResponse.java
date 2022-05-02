package com.autotest.LiuMa.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TestCaseApiResponse extends TestCaseResponse{

    @JSONField(ordinal = 6)
    private List<TestCaseApiDataResponse> apiList; // API用例的接口列表

}
