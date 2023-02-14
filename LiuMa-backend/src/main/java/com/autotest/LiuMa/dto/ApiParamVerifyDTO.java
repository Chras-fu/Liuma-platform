package com.autotest.LiuMa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiParamVerifyDTO {

    private String name; // 参数名称

    private String direction; // 正向 逆向

    private Boolean delete=false; // 是否删除 默认不删

    private String type; // 参数类型

    private String description; // 参数描述

    private Object value; // 参数值
}
