package com.autotest.LiuMa.common.response;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.autotest.LiuMa.common.constants.ResponseCode;
import com.autotest.LiuMa.common.exception.*;
import com.autotest.LiuMa.response.TemplateResponse;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(LMException.class)
    public TemplateResponse<String> LMExceptionHandler(LMException e) {
        // 未知的失败
        return new TemplateResponse<>(ResponseCode.FAILED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public TemplateResponse<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new TemplateResponse<>(ResponseCode.VALIDATE_FAILED, objectError.getDefaultMessage());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public TemplateResponse<String> TokenVerifyExceptionHandler(JWTVerificationException e) {
        return new TemplateResponse<>(ResponseCode.TOKEN_FAILED, e.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public TemplateResponse<String> TokenExpireExceptionHandler(TokenExpiredException e) {
        return new TemplateResponse<>(ResponseCode.TOKEN_EXPIRE, e.getMessage());
    }

    @ExceptionHandler(TokenEmptyException.class)
    public TemplateResponse<String> NonLoginExceptionHandler(TokenEmptyException e) {
        return new TemplateResponse<>(ResponseCode.TOKEN_EMPTY, e.getMessage());
    }

    @ExceptionHandler(LoginVerifyException.class)
    public TemplateResponse<String> LoginVerifyExceptionHandler(LoginVerifyException e) {
        return new TemplateResponse<>(ResponseCode.LOGIN_FAILED, e.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    public TemplateResponse<String> DuplicateContentExceptionHandler(DuplicateException e) {
        return new TemplateResponse<>(ResponseCode.DUPLICATE_FAILED, e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public TemplateResponse<String> FileUploadExceptionHandler(FileUploadException e) {
        return new TemplateResponse<>(ResponseCode.UPLOAD_FAILED, e.getMessage());
    }

    @ExceptionHandler(EngineVerifyException.class)
    public TemplateResponse<String> EngineVerifyExceptionHandler(EngineVerifyException e) {
        return new TemplateResponse<>(ResponseCode.ENGINE_FAILED, e.getMessage());
    }

    @ExceptionHandler(PwdVerifyException.class)
    public TemplateResponse<String> PasswordVerifyExceptionHandler(PwdVerifyException e) {
        return new TemplateResponse<>(ResponseCode.PASSWORD_FAILED, e.getMessage());
    }
}
