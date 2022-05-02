package com.autotest.LiuMa.common.constants;

public enum ReportSourceType {
    // 用来区分执行数据从哪儿取
    PLAN("plan"),
    COLLECTION("collection"),
    CASE("case"),
    TEMP("temp"); // 调试任务 从debug_data临时表取

    private final String value;
    ReportSourceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
