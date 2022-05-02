package com.autotest.LiuMa.common.constants;

public enum EngineStatus {
    OFFLINE("offline"), ONLINE("online"), RUNNING("running");

    private final String value;

    EngineStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
