package com.autotest.LiuMa.common.exception;


public class TokenVerifyException extends RuntimeException{
    public TokenVerifyException(String message) {
        super(message);
    }

    private TokenVerifyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new TokenVerifyException(message);
    }

    public static TokenVerifyException getException(String message) {
        throw new TokenVerifyException(message);
    }

    public static void throwException(Throwable t) {
        throw new TokenVerifyException(t);
    }

}
