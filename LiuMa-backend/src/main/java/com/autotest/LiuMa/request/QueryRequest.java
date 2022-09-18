package com.autotest.LiuMa.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryRequest {
    private String condition;   // 模糊查找输入值
    private String moduleId;
    private String createUser;
    private String projectId;

    private String caseType; // 查找用例时用例类型 API/WEB/APP
    private String collectionId; //查找报告时报告所属集合Id
    private String planId; //查找报告时报告所属计划Id
    private String operationType; // 查找控件时控件类型
    private String roleId; // 查找角色用户列表时所属角色Id
    private String requestUser; // 请求人
    private String uiType; // 查找操作时ui类型 web/app
    private String system;  // 查找app控件和操作所属系统 android/apple
    private String status;  // 设备状态
    private JSONObject filter;  // 设备查询条件
}
