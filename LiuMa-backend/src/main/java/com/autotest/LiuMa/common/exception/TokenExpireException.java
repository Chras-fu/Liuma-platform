package com.autotest.LiuMa.common.exception;


public class TokenExpireException extends RuntimeException{
    public TokenExpireException(String message) {
        super(message);
    }

    private TokenExpireException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new TokenExpireException(message);
    }

    public static TokenExpireException getException(String message) {
        throw new TokenExpireException(message);
    }

    public static void throwException(Throwable t) {
        throw new TokenExpireException(t);
    }

}
