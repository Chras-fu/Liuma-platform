package com.autotest.LiuMa.common.constants;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS(0, "成功"),

    FAILED(1000, "失败"),

    VALIDATE_FAILED(1002, "参数校验失败"),

    DUPLICATE_FAILED(1003, "重复校验失败"),

    LOGIN_FAILED(2010, "登录失败"),

    PASSWORD_FAILED(2015, "密码错误"),

    TOKEN_EMPTY(2020, "登录信息获取失败 请重新登录"),

    TOKEN_EXPIRE(2030, "登录已过期 请重新登录"),

    TOKEN_FAILED(2040, "登录信息校验失败 请重新登录"),

    ENGINE_FAILED(2050, "引擎code及secret验证失败"),

    UPLOAD_FAILED(3010, "文件上传失败"),

    ERROR(5000, "未知错误");

    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
