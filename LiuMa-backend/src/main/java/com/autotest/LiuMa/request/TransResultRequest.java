package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransResultRequest {
    private String id;

    private String name;

    private String content;

    private String description;

    private String log;

    private Integer during;

    private Integer status;

    private List<String> screenShotList;

}
