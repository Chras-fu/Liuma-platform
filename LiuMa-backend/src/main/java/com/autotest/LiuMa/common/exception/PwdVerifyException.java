package com.autotest.LiuMa.common.exception;


public class PwdVerifyException extends RuntimeException{
    public PwdVerifyException(String message) {
        super(message);
    }

    private PwdVerifyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new PwdVerifyException(message);
    }

    public static PwdVerifyException getException(String message) {
        throw new PwdVerifyException(message);
    }

    public static void throwException(Throwable t) {
        throw new PwdVerifyException(t);
    }

}
