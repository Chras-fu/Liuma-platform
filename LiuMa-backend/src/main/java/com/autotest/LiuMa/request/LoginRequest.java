package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String account;
    private String password;
}
