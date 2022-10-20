package com.ads.mini_project.util;

import com.ads.mini_project.pojo.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.ads.mini_project.util.Constant.SUCCESS_STATUS;
import static com.ads.mini_project.util.Constant.SUCCESS_STATUS_CODE;

@Slf4j
public class Util {
    public static ResponseEntity<GenericResponse> prepareSuccessResponse(String methodName, String message, Object data) {
        log.info("[{}]: Responding with message: [{}], data: [{}]", methodName, message, data);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.builder()
                        .status(SUCCESS_STATUS)
                        .statusCode(SUCCESS_STATUS_CODE)
                        .message(message)
                        .payload(data)
                        .build());
    }

    public static ResponseEntity<GenericResponse> prepareErrorResponse(HttpStatus httpStatus, String errorCode, String message, Object data) {
        return ResponseEntity.status(httpStatus)
                .body(GenericResponse.builder()
                        .status(httpStatus.name())
                        .statusCode(errorCode)
                        .message(message)
                        .payload(data)
                        .build());
    }
}
