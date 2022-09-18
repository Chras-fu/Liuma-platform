package com.autotest.LiuMa.common.constants;

import java.util.ArrayList;
import java.util.List;

public enum OperationType {
    BROWSER("browser", "浏览器"),
    SYSTEM("system", "系统"),
    PAGE("page", "页面"),
    RELATION("relation", "关联"),
    ASSERTION("assertion", "断言"),
    CONDITION("condition", "条件"),
    SCENARIO("scenario", "场景");

    private final String name;
    private final String label;

    OperationType(String name, String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String toLabel(){
        return this.label;
    }

    public static List<String> enumList(String type){
        List<String> enumList = new ArrayList<>();
        if(type.equals("web")){
            enumList.add(BROWSER.name);
        }else {
            enumList.add(SYSTEM.name);
        }
        enumList.add(PAGE.name);
        enumList.add(RELATION.name);
        enumList.add(ASSERTION.name);
        enumList.add(CONDITION.name);
        enumList.add(SCENARIO.name);
        return enumList;
    }
}
