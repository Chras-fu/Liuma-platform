package com.autotest.LiuMa.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestCaseApiDataResponse {

    @JSONField(ordinal = 1)
    private String apiId;

    @JSONField(ordinal = 2)
    private String apiName;

    @JSONField(ordinal = 3)
    private String url;

    @JSONField(ordinal = 4)
    private String path;

    @JSONField(ordinal = 5)
    private String method;

    @JSONField(ordinal = 6)
    private String protocol;

    @JSONField(ordinal = 7)
    private JSONObject headers;

    @JSONField(ordinal = 8)
    private JSONObject proxies;

    @JSONField(ordinal = 9)
    private JSONObject body;

    @JSONField(ordinal = 10)
    private JSONObject query;

    @JSONField(ordinal = 11)
    private JSONObject rest;

    @JSONField(ordinal = 12)
    private JSONArray assertions;

    @JSONField(ordinal = 13)
    private JSONArray relations;

    @JSONField(ordinal = 14)
    private JSONObject controller;

}
