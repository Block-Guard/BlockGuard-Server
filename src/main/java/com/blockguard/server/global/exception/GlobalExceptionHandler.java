package com.blockguard.server.global.exception;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("유효성 검사에 실패했습니다.");

        log.warn("[ValidationException] message: {}", message);

        if (message.contains("이메일 형식")) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_EMAIL_TYPE.getStatus())
                    .body(ErrorResponse.of(ErrorCode.INVALID_EMAIL_TYPE));
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException maxUploadSizeExceededException) {
        log.error("[MaxUploadSizeExceeded] message: {}", maxUploadSizeExceededException.getMessage());
        return ResponseEntity
                .status(ErrorCode.FILE_SIZE_EXCEEDED.getStatus())
                .body(ErrorResponse.of(ErrorCode.FILE_SIZE_EXCEEDED));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("[Exception] message: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
