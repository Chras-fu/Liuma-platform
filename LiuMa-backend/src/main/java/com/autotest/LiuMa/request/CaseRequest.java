package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class CaseRequest {
    private String id;

    private Long num;

    private String name;

    private String level;

    private String moduleId;

    private String moduleName;

    private String projectId;

    private String type;

    private String thirdParty;

    private String description;

    private JSONArray environmentIds;

    private String system;

    private JSONObject commonParam;

    private Long createTime;

    private Long updateTime;

    private String createUser;

    private String updateUser;

    private String status;

    private List<CaseApiRequest> caseApis;

    private List<CaseWebRequest> caseWebs;

    private List<CaseAppRequest> caseApps;
}
