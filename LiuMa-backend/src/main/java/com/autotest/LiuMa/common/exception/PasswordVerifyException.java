package com.autotest.LiuMa.common.exception;


public class PasswordVerifyException extends RuntimeException{
    public PasswordVerifyException(String message) {
        super(message);
    }

    private PasswordVerifyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new PasswordVerifyException(message);
    }

    public static PasswordVerifyException getException(String message) {
        throw new PasswordVerifyException(message);
    }

    public static void throwException(Throwable t) {
        throw new PasswordVerifyException(t);
    }

}
