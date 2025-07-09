package com.blockguard.server.global.common.response;

import com.blockguard.server.global.common.codes.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> of(SuccessCode code, T data) {
        return BaseResponse.<T>builder()
                .code(code.getCode())
                .msg(code.getMsg())
                .data(data)
                .build();
    }

    public static BaseResponse<Void> of(SuccessCode code) {
        return BaseResponse.<Void>builder()
                .code(code.getCode())
                .msg(code.getMsg())
                .data(null)
                .build();
    }
}
