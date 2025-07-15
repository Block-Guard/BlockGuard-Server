package com.blockguard.server.global.common.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    OK(HttpStatus.OK, 2000, "OK"),
    USER_REGISTERED(HttpStatus.CREATED, 2001, "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, 2002, "로그인 성공"),
    GET_MY_PAGE_SUCCESS(HttpStatus.OK, 2004, "마이페이지 조회에 성공하였습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;
}
