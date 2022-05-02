package com.autotest.LiuMa.common.exception;


public class DuplicateContentException extends RuntimeException{
    public DuplicateContentException(String message) {
        super(message);
    }

    private DuplicateContentException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new DuplicateContentException(message);
    }

    public static DuplicateContentException getException(String message) {
        throw new DuplicateContentException(message);
    }

    public static void throwException(Throwable t) {
        throw new DuplicateContentException(t);
    }

}
