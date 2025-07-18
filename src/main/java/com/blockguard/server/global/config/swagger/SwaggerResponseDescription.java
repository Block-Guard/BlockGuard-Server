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
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED
    ))),

    FIND_PASSWORD_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.EMAIL_NOT_FOUND
    ))),

    FIND_GUARDIANS_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.USER_INFO_NOT_FOUND
    ))),

    CREATE_GUARDIAN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.DUPLICATE_GUARDIAN_NAME
    ))),

    UPDATE_GUARDIAN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.GUARDIAN_NOT_FOUND,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.DUPLICATE_GUARDIAN_NAME
    ))),

    UPDATE_GUARDIAN_PRIMARY_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.GUARDIAN_NOT_FOUND
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
