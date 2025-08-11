package com.blockguard.server.global.config.swagger;

import com.blockguard.server.global.common.codes.ErrorCode;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public enum SwaggerResponseDescription {
    REGISTER_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.DUPLICATED_EMAIL,
            ErrorCode.INVALID_EMAIL_TYPE,
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_DATE_FORMAT
    ))),

    LOGIN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_EMAIL,
            ErrorCode.INVALID_PASSWORD
    ))),

    FIND_EMAIL_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.USER_INFO_NOT_FOUND,
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_DATE_FORMAT
    ))),

    UPDATE_MY_PAGE_INFO_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_DATE_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.INVALID_TOKEN,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.UPDATE_PROFILE_CONFLICT
    ))),

    UPDATE_PASSWORD_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.PASSWORD_MISMATCH
    ))),

    FIND_PASSWORD_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.EMAIL_NOT_FOUND,
            ErrorCode.INVALID_EMAIL_TYPE
    ))),

    FIND_GUARDIANS_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.USER_INFO_NOT_FOUND
    ))),

    CREATE_GUARDIAN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.DUPLICATE_GUARDIAN_NAME
    ))),

    UPDATE_GUARDIAN_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.INVALID_PHONE_NUMBER_FORMAT,
            ErrorCode.INVALID_PROFILE_IMAGE,
            ErrorCode.GUARDIAN_NOT_FOUND,
            ErrorCode.FILE_NAME_NOT_FOUND,
            ErrorCode.INVALID_DIRECTORY_ROUTE,
            ErrorCode.FILE_SIZE_EXCEEDED,
            ErrorCode.DUPLICATE_GUARDIAN_NAME,
            ErrorCode.UPDATE_PROFILE_CONFLICT
    ))),

    UPDATE_GUARDIAN_PRIMARY_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.GUARDIAN_NOT_FOUND
    ))),

    CHECK_EMAIL_DUPLICATED_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_EMAIL_TYPE
    ))),

    CREATE_REPORT_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.REPORT_ALREADY_IN_PROGRESS
    ))),

    GET_STEP_INFO_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.REPORT_NOT_FOUND,
            ErrorCode.INVALID_STEP
    ))),

    GET_NEWS_ARTICLES_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.FAIL_TO_CRAWLING_NEWS
    ))),

    UPDATE_STEP_INFO_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN,
            ErrorCode.REPORT_NOT_FOUND,
            ErrorCode.INVALID_STEP,
            ErrorCode.INVALID_CHECKBOX_COUNT,
            ErrorCode.INVALID_STEP_COMPLETION,
            ErrorCode.REPORT_STEP_ALREADY_COMPLETED
    ))),

    CHECK_FRAUD_PHONE_NUMBER_FAIL(new LinkedHashSet<>(Set.of(
            ErrorCode.FRAUD_NUMBER_SERVER_ERROR
    ))),

    INVALID_TOKEN(new LinkedHashSet<>(Set.of(
            ErrorCode.INVALID_TOKEN
    )));

    private final Set<ErrorCode> errorCodeList;

    SwaggerResponseDescription(Set<ErrorCode> errorCodes) {
        // 공통 에러 추가
        errorCodes.add(
                ErrorCode.INTERNAL_SERVER_ERROR
        );

        this.errorCodeList = errorCodes;
    }
}
