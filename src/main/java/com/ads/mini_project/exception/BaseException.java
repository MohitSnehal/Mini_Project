package com.ads.mini_project.exception;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public class BaseException extends RuntimeException {
    private int status;
    private String message;
    private String errorCode;

    public BaseException(int status, String message, String errorCode) {
        super(message);
        log.debug("SocialGraphBaseException status, message, errorCode: {}, {}, {}", new Object[]{status, message, errorCode});
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public BaseException(int status, String message) {
        super(message);
        log.debug("SocialGraphBaseException status, message: {}, {}", status, message);
        this.status = status;
        this.message = message;
    }

    public BaseException() {
        super();
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
