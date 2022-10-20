package com.ads.mini_project.exception;

import com.ads.mini_project.pojo.GenericResponse;
import com.ads.mini_project.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.ads.mini_project.util.Constant.INTERNAL_SERVER_ERROR_CODE;
import static com.ads.mini_project.util.Constant.INTERNAL_SERVER_ERROR_MESSAGE;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse> handleServerExceptions(Exception ex) {
        log.error("Exception ", ex);
        return Util.prepareErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MESSAGE, null);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<GenericResponse> handleSGBaseException(BaseException ex) {
        log.error("SocialGraphBaseException ", ex);
        return Util.prepareErrorResponse(HttpStatus.valueOf(ex.getStatus()), ex.getErrorCode(), ex.getMessage(), null);
    }

}

