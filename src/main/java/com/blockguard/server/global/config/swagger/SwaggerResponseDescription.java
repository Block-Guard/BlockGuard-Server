package com.blockguard.server.global.config.swagger;

import com.blockguard.server.global.common.codes.ErrorCode;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public enum SwaggerResponseDescription {
    REGISTER_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.DUPLICATED_EMAIL
    ))),

    LOGIN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_EMAIL,
            ErrorCode.INVALID_PASSWORD
    ))),

    FIND_EMAIL_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.USER_INFO_NOT_FOUND
    ))),

    UPDATE_MY_PAGE_INFO_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_DATE_FORMAT
    ))),

    FIND_PASSWORD_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.EMAIL_NOT_FOUND
    )));




    private final Set<ErrorCode> errorCodeList;

    SwaggerResponseDescription(Set<ErrorCode> errorCodes) {
        // 공통 에러 추가
        errorCodes.addAll(Set.of(
                ErrorCode.INVALID_TOKEN,
                ErrorCode.INTERNAL_SERVER_ERROR
        ));

        this.errorCodeList = errorCodes;
    }
}
