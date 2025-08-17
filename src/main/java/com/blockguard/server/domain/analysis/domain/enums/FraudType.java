package com.blockguard.server.domain.analysis.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public enum FraudType {
    INSTITUTION_IMPERSONATION("기관 사칭형"),
    LOAN_FRAUD               ("대출 사기형"),
    CARD_COMPANY_IMPERSONATION("카드사 사칭형"),
    FAMILY_IMPERSONATION      ("가족/지인 사칭형"),
    FIRST_BIRTHDAY_INVITE     ("돌잔치 초대장형"),
    MOBILE_WEDDING_INVITE     ("모바일 청첩장형"),
    OBITUARY_MESSAGE          ("부고 문자형"),
    FINE_AND_PENALTY          ("범칙금/과태료 부과형"),
    HEALTH_INSURANCE_IMPERSONATION("건강보험공단 사칭형"),
    POLICE_SUMMONS            ("경찰 출석 요구형"),
    NATIONAL_TAX_SERVICE      ("국세청 사칭형"),
    PART_TIME_SCAM            ("알바/부업 사기형"),
    GOVERNMENT_GRANT_SCAM     ("정부 지원금 위장형"),
    DELIVERY_SCAM             ("택배 사기형"),
    CRYPTOCURRENCY_SCAM       ("가상화폐 사기형"),
    STOCK_INVESTMENT_SCAM     ("주식투자 사기형"),
    IPO_SCAM                  ("청약 공모주 사기형"),
    FALSE_PAYMENT_SCAM        ("허위결제 사기형"),
    UNKNOWN("알 수 없음");

    private final String korName;

    FraudType(String korName) {
        this.korName = korName;
    }

    // 공백(ASCII/유니코드 구분자 포함) 전부 제거
    private static final Pattern WS = Pattern.compile("[\\s\\p{Z}]+");

    private static String squashSpaces(String s) {
        return WS.matcher(s).replaceAll("");
    }

    public static FraudType fromKoreanName(String kr) {
        if (kr == null) {
            return UNKNOWN;
        }
        final String target = squashSpaces(kr.trim());

        return Arrays.stream(values())
                .filter(e -> squashSpaces(e.korName).equals(target))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
