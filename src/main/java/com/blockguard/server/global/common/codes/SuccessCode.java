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
    GUARDIAN_UPDATED(HttpStatus.OK, 2011, "보호자 정보 수정이 완료되었습니다."),
    GUARDIAN_PRIMARY_UPDATED(HttpStatus.OK, 2012, "보호자 정보 수정이 완료되었습니다."),
    GUARDIAN_DELETED(HttpStatus.NO_CONTENT, 2013, "보호자 삭제 처리되었습니다."),
    CHECK_EMAIL_DUPLICATED(HttpStatus.OK, 2014, "이메일 중복 확인이 완료되었습니다."),
    ANALYZE_FRAUD_SUCCESS(HttpStatus.OK, 2015, "사기 분석이 완료되었습니다."),
    CHECK_URL_FRAUD_SUCCESS(HttpStatus.OK, 2016, "URL 사기 조회가 완료되었습니다."),
    CHECK_PHONE_NUMBER_FRAUD_SUCCESS(HttpStatus.OK, 2017, "전화번호 사기 조회가 완료되었습니다."),
    IMPORT_OPEN_API_SUCCESS(HttpStatus.OK, 2018, "OPEN API 데이터 호출이 완료되었습니다."),
    CREATE_REPORT_RECORD_SUCCESS(HttpStatus.CREATED,2019, "신고 현황이 생성되었습니다."),
    CURRENT_REPORT_FOUND(HttpStatus.OK,2020, "진행중인 신고 현황 조회에 성공하였습니다."),
    STEP_INFO_FOUND(HttpStatus.OK,2021, "신고 단계 정보 조회에 성공하였습니다."),
    UPDATE_STEP_INFO_SUCCESS(HttpStatus.OK,2022, "신고 단계 정보가 성공적으로 업데이트 되었습니다."),
    GET_NEWS_ARTICLES_SUCCESS(HttpStatus.OK, 2023, "뉴스 목록 조회가 완료되었습니다."),
    CRWAL_DAUM_NEWS_SUCCESS(HttpStatus.OK, 2024, "뉴스 크롤링이 완료되었습니다.");

    private final HttpStatus status;
    private final int code;
    private final String msg;
}
