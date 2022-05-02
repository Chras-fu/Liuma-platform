package com.autotest.LiuMa.common.constants;

public enum PlanFrequency {
    ONLY_ONE("ONLY_ONE"),
    HALF_HOUR("HALF_HOUR"),
    ONE_HOUR("ONE_HOUR"),
    HALF_DAY("HALF_DAY"),
    ONE_DAY("ONE_DAY"),
    ONE_WEEK("ONE_WEEK"),
    ONE_MONTH("ONE_MONTH");

    private final String value;
    PlanFrequency(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
