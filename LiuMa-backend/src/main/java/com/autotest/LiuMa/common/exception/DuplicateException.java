package com.autotest.LiuMa.common.exception;


public class DuplicateException extends RuntimeException{
    public DuplicateException(String message) {
        super(message);
    }

    private DuplicateException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new DuplicateException(message);
    }

    public static DuplicateException getException(String message) {
        throw new DuplicateException(message);
    }

    public static void throwException(Throwable t) {
        throw new DuplicateException(t);
    }

}
