package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CaseAppRequest {
    private String id;

    private Long index;

    private String caseId;

    private String operationId;

    private String description;

    private JSONArray element;

    private JSONArray data;

}
