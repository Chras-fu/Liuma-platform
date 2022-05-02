package com.autotest.LiuMa.common.constants;

import java.util.ArrayList;
import java.util.List;

public enum OperationType {
    BROWSER("browser", "浏览器"),
    PAGE("page", "网页"),
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

    public static List<String> enumList(){
        List<String> enumList = new ArrayList<>();
        enumList.add(BROWSER.name);
        enumList.add(PAGE.name);
        enumList.add(RELATION.name);
        enumList.add(ASSERTION.name);
        enumList.add(CONDITION.name);
        enumList.add(SCENARIO.name);
        return enumList;
    }
}
