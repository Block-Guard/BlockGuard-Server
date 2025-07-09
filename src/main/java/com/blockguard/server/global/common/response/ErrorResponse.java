package com.blockguard.server.global.common.response;

import com.blockguard.server.global.common.codes.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final int code;
    private final String msg;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .msg(errorCode.getMsg())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String customMsg) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .msg(customMsg)
                .build();
    }
}
