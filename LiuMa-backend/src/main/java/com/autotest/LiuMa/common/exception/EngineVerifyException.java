package com.autotest.LiuMa.common.exception;


public class EngineVerifyException extends RuntimeException{
    public EngineVerifyException(String message) {
        super(message);
    }

    private EngineVerifyException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new EngineVerifyException(message);
    }

    public static EngineVerifyException getException(String message) {
        throw new EngineVerifyException(message);
    }

    public static void throwException(Throwable t) {
        throw new EngineVerifyException(t);
    }

}
