package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiRequest {
    private String id;

    private String name;

    private String level;

    private String moduleId;

    private String projectId;

    private String method;

    private String path;

    private String protocol;

    private String domainSign;

    private String description;

    private JSONArray header;

    private JSONObject body;

    private JSONArray query;

    private JSONArray rest;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private String status;
}
