package com.autotest.LiuMa.common.constants;

public enum DeviceStatus {
    OFFLINE("offline"), ONLINE("online"), USING("using"), COLDING("colding"), TESTING("testing");

    private final String value;

    DeviceStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
