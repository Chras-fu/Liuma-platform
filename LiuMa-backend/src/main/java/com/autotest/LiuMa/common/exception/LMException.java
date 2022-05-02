package com.autotest.LiuMa.common.exception;


public class LMException extends RuntimeException{
    public LMException(String message) {
        super(message);
    }

    private LMException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new LMException(message);
    }

    public static LMException getException(String message) {
        throw new LMException(message);
    }

    public static void throwException(Throwable t) {
        throw new LMException(t);
    }

}
