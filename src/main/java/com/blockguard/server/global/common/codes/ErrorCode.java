package com.blockguard.server.global.common.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /// 4000 ~ : client error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 4000, "잘못된 요청 형식입니다. 입력값을 확인해주세요."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, 4001, "이미 가입된 이메일입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, 4002, "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 4003, "비밀번호가 일치하지 않습니다."),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, 4004, "일치하는 회원 정보가 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 4005, "일치하는 이메일 정보가 없습니다."),
    INVALID_USER(HttpStatus.NOT_FOUND, 4006, "존재하지 않는 회원입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, 4007, "생년월일 형식이 올바르지 않습니다. yyyyMMDD 형식으로 입력해주십시오."),
    INVALID_PHONE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, 4008, "전화번호 형식이 올바르지 않습니다. 010-1234-5678 형식으로 입력해주십시오."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, 4009, "현재 비밀번호 값이 일치하지 않습니다."),
    INVALID_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, 4010,"이미지 파일만 업로드 가능합니다."),
    GUARDIAN_NOT_FOUND(HttpStatus.NOT_FOUND, 4011, "일치하는 보호자 정보가 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 4020, "유효하지 않은 토큰입니다."),
    FILE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, 4021, "파일명이 없습니다."),
    INVALID_DIRECTORY_ROUTE(HttpStatus.NOT_FOUND, 4022, "잘못된 디렉토리 경로입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, 4023, "프로필 파일 최대 허용 용량(5MB)을 초과했습니다."),
    DUPLICATE_GUARDIAN_NAME(HttpStatus.BAD_REQUEST, 4024, "이미 등록된 보호자 이름입니다."),
    INVALID_EMAIL_TYPE(HttpStatus.BAD_REQUEST, 4025, "이메일 형식이 올바르지 않습니다."),
    IMAGE_UPLOAD_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, 4026, "이미지는 최대 2장까지만 업로드 가능합니다."),
    FAIL_IMPORT_OPEN_API(HttpStatus.BAD_REQUEST, 4027, "OPEN API 호출에 실패하였습니다."),

    // 5000~ : server error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;

}
