package com.blockguard.server.global.exception;

import com.blockguard.server.global.common.codes.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessExceptionHandler extends RuntimeException{
    private final ErrorCode errorCode;

}
