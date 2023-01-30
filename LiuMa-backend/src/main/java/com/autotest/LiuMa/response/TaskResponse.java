package com.autotest.LiuMa.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskResponse {

    private String taskId;

    private String taskType;

    private String downloadUrl;

    private Integer maxThread; // 最大并发数

    private Boolean reRun; // 是否失败重试

    private List<TaskTestCollectionResponse> testCollectionList;    // 非调试执行

    private JSONObject debugData; // 调试执行的用例数据

}
