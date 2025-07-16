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
    SEND_TEMP_PASSWORD_BY_EMAIL(HttpStatus.OK, 2004, "임시 비밀번호를 메일로 전송하였습니다."),
    GET_MY_PAGE_SUCCESS(HttpStatus.OK, 2005, "마이페이지 조회에 성공하였습니다."),
    UPDATE_USER_INFO_SUCCESS(HttpStatus.OK, 2006, "회원 정보를 수정하였습니다."),
    WITHDRAW_SUCCESS(HttpStatus.OK, 2007, "회원 탈퇴가 성공하였습니다."),
    UPDATE_PASSWORD_SUCCESS(HttpStatus.OK,2008,"비밀번호 변경에 성공하였습니다."),
    GUARDIAN_REGISTERED(HttpStatus.CREATED,2009,"보호자 등록이 완료되었습니다."),
    GUARDIAN_LIST_RETRIEVED(HttpStatus.OK, 2010, "보호자 목록 조회가 완료되었습니다."),
    GUARDIAN_UPDATED(HttpStatus.OK, 2011, "보호자 정보 수정이 완료되었습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;
}
