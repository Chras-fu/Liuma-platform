package com.autotest.LiuMa.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskTestCaseResponse {
    private Long index; // 用例在集合中的顺序

    private String caseId;

    private String caseType;

}
