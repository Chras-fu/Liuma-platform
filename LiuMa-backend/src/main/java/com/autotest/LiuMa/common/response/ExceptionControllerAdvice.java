package com.autotest.LiuMa.common.response;
import com.autotest.LiuMa.common.exception.*;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(LMException.class)
    public ResponseTemp<String> LMExceptionHandler(LMException e) {
        // 未知的失败
        return new ResponseTemp<>(ResponseCode.FAILED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseTemp<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResponseTemp<>(ResponseCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }

    @ExceptionHandler(TokenVerifyException.class)
    public ResponseTemp<String> TokenVerifyExceptionHandler(TokenVerifyException e) {
        return new ResponseTemp<>(ResponseCode.TOKEN_FAILED, e.getMessage());
    }

    @ExceptionHandler(TokenExpireException.class)
    public ResponseTemp<String> TokenExpireExceptionHandler(TokenExpireException e) {
        return new ResponseTemp<>(ResponseCode.TOKEN_EXPIRE, e.getMessage());
    }

    @ExceptionHandler(TokenEmptyException.class)
    public ResponseTemp<String> NonLoginExceptionHandler(TokenEmptyException e) {
        return new ResponseTemp<>(ResponseCode.TOKEN_EMPTY, e.getMessage());
    }

    @ExceptionHandler(LoginVerifyException.class)
    public ResponseTemp<String> LoginVerifyExceptionHandler(LoginVerifyException e) {
        return new ResponseTemp<>(ResponseCode.LOGIN_FAILED, e.getMessage());
    }

    @ExceptionHandler(DuplicateContentException.class)
    public ResponseTemp<String> DuplicateContentExceptionHandler(DuplicateContentException e) {
        return new ResponseTemp<>(ResponseCode.DUPLICATE_FAILED, e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseTemp<String> FileUploadExceptionHandler(FileUploadException e) {
        return new ResponseTemp<>(ResponseCode.UPLOAD_FAILED, e.getMessage());
    }

    @ExceptionHandler(EngineVerifyException.class)
    public ResponseTemp<String> EngineVerifyExceptionHandler(EngineVerifyException e) {
        return new ResponseTemp<>(ResponseCode.ENGINE_FAILED, e.getMessage());
    }

    @ExceptionHandler(PasswordVerifyException.class)
    public ResponseTemp<String> PasswordVerifyExceptionHandler(PasswordVerifyException e) {
        return new ResponseTemp<>(ResponseCode.PASSWORD_FAILED, e.getMessage());
    }
}
