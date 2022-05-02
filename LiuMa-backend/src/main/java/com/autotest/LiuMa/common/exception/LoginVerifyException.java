package com.autotest.LiuMa.common.exception;


public class LoginVerifyException extends RuntimeException{
    public LoginVerifyException(String message) {
        super(message);
    }

    private LoginVerifyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new LoginVerifyException(message);
    }

    public static LoginVerifyException getException(String message) {
        throw new LoginVerifyException(message);
    }

    public static void throwException(Throwable t) {
        throw new LoginVerifyException(t);
    }

}
