package com.blockguard.server.global.exception;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessExceptionHandler.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(BusinessExceptionHandler customException) {
        ErrorCode errorCode = customException.getErrorCode();
        log.error("[CustomException] code: {}, message: {}", errorCode.getCode(), errorCode.getMsg());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("[Exception] message: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
