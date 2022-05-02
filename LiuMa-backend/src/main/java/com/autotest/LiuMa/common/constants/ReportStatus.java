package com.autotest.LiuMa.common.constants;

public enum ReportStatus {
    PREPARED("prepared"),   // 已就绪 等待引擎执行
    RUNNING("running"),     // 执行中
    DISCONTINUE("discontinue"),    // 执行超时或者手动终止
    COMPLETED("completed"),  // 执行完成
    SUCCESS("success"),    // 全部用例成功 则为成功
    FAIL("fail"),   // 只有断言失败 则为失败
    ERROR("error"),     // 网络请求等错误 则为错误
    SKIP("skip");  // 用例跳过执行

    private final String value;
    ReportStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
