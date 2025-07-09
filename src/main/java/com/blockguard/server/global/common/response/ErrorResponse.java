package com.blockguard.server.global.common.response;

import com.blockguard.server.global.common.codes.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "xxxx")
    private final int code;
    @Schema(description = "에러 메세지", example = "예시 에러 메세지입니다.")
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
