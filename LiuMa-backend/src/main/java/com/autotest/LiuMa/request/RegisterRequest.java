package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    private String account;

    private Long mobile;

    private String username;

    private String email;

    private String password;

}
