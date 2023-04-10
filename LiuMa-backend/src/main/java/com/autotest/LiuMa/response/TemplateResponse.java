package com.autotest.LiuMa.response;

import com.autotest.LiuMa.common.constants.ResponseCode;
import lombok.Data;

@Data
public class TemplateResponse<T> {

    private int status;

    private String message;

    private T data;

    public TemplateResponse(T data) {
        this(ResponseCode.SUCCESS, data);
    }

    public TemplateResponse(ResponseCode resultCode, T data) {
        this.status = resultCode.getStatus();
        this.message = resultCode.getMessage();
        this.data = data;
    }
}