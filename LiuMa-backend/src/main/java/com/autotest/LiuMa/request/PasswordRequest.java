package com.autotest.LiuMa.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordRequest {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
