package com.autotest.LiuMa.common.constants;

public enum TaskType {
    // 用来区分任务优先级 内置引擎调试优先级最高 其次是手动 再定时 自定义引擎默认时间顺序
    DEBUG("debug"),     // 用例调试或执行
    RUN("run"),     // 集合或计划手动执行
    SCHEDULE("schedule"),   // 计划定时任务执行
    API("api");   // 计划外部调用执行

    private final String value;
    TaskType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
