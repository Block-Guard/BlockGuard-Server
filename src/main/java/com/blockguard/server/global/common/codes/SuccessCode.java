package com.blockguard.server.global.common.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    OK(HttpStatus.OK, 2000, "OK"),
    USER_REGISTERED(HttpStatus.CREATED, 2001, "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, 2002, "로그인에 성공하였습니다."),
    USER_EMAIL_FOUND(HttpStatus.OK, 2003, "아이디 조회에 성공하였습니다."),
    SEND_TEMP_PASSWORD_BY_EMAIL(HttpStatus.OK, 2005, "임시 비밀번호를 메일로 전송하였습니다."),
    GUARDIANS_FOUND(HttpStatus.OK, 2010, "보호자 목록 조회가 완료되었습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;
}
