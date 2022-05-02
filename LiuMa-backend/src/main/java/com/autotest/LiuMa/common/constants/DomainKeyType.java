package com.autotest.LiuMa.common.constants;

public enum DomainKeyType {
    SIGN("sign"), PATH("path");

    private final String value;

    DomainKeyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
