package com.autotest.LiuMa.common.constants;

public enum NotificationStatus {
    ENABLE("enable"), DISABLE("disable");

    private final String value;

    NotificationStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
