package com.autotest.LiuMa.common.exception;


public class TokenEmptyException extends RuntimeException{
    public TokenEmptyException(String message) {
        super(message);
    }

    private TokenEmptyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new TokenEmptyException(message);
    }

    public static TokenEmptyException getException(String message) {
        throw new TokenEmptyException(message);
    }

    public static void throwException(Throwable t) {
        throw new TokenEmptyException(t);
    }

}
