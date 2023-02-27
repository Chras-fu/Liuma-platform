package com.autotest.LiuMa.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskTestCollectionResponse {

    private String collectionId;

    private String deviceId;

    private List<TaskTestCaseResponse> testCaseList;

}
