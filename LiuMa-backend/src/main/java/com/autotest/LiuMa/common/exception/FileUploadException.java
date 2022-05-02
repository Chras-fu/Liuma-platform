package com.autotest.LiuMa.common.exception;


public class FileUploadException extends RuntimeException{
    public FileUploadException(String message) {
        super(message);
    }

    private FileUploadException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new FileUploadException(message);
    }

    public static FileUploadException getException(String message) {
        throw new FileUploadException(message);
    }

    public static void throwException(Throwable t) {
        throw new FileUploadException(t);
    }

}
