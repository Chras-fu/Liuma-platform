package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CaseWebRequest {
    private String id;

    private Long index;

    private String caseId;

    private String operationId;

    private String description;

    private JSONArray element;

    private JSONArray data;

}
