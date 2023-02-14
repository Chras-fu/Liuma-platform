package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiParamRuleDTO{
    private String name;    // 参数名

    private String type;    // 参数数据类型

    private String required;    // 必填性校验类型

    private String random;  // 参数范围 与type结合使用

    private String value; // 参数默认值
}
