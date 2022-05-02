package com.autotest.LiuMa.common.response;

import lombok.Data;

@Data
public class ResponseTemp<T> {

    private int status;

    private String message;

    private T data;

    public ResponseTemp(T data) {
        this(ResponseCode.SUCCESS, data);
    }

    public ResponseTemp(ResponseCode resultCode, T data) {
        this.status = resultCode.getStatus();
        this.message = resultCode.getMessage();
        this.data = data;
    }
}