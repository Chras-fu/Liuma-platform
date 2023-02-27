package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CaseApiRequest {
    private String id;

    private Long index;

    private String caseId;

    private String apiId;

    private String description;

    private JSONArray header;

    private JSONObject body;

    private JSONArray query;

    private JSONArray rest;

    private JSONArray assertion;

    private JSONArray relation;

    private JSONArray controller;

}
