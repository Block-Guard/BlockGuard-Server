package com.blockguard.server.domain.analysis.domain.enums;

import lombok.Getter;

@Getter
public enum FraudType {
    VOICE_PHISHING           ("보이스피싱"),
    MESSENGER_PHISHING       ("메신저피싱"),
    SMISHING                 ("스미싱"),
    INSTITUTION_IMPERSONATION("기관사칭형"),
    LOAN_FRAUD               ("대출사기형"),
    CARD_COMPANY_IMPERSONATION("카드사 사칭형"),
    FAMILY_IMPERSONATION      ("가족/지인사칭형"),
    CELEBRATORY_IMPERSONATION("경조사사칭형"),
    FIRST_BIRTHDAY_INVITE     ("돌잔치 초대장형"),
    MOBILE_WEDDING_INVITE     ("모바일 청첩장형"),
    OBITUARY_MESSAGE          ("부고문자형"),
    PUBLIC_AGENCY_IMPERSONATION("공공기관 사칭형"),
    FINE_AND_PENALTY          ("범칙금/과태료 부과형"),
    HEALTH_INSURANCE_IMPERSONATION("건강보험공단 사칭형"),
    POLICE_SUMMONS            ("경찰출석요구형"),
    NATIONAL_TAX_SERVICE      ("국세청 사칭형"),
    PART_TIME_SCAM            ("알바/부업 사기형"),
    GOVERNMENT_GRANT_SCAM     ("정부지원금 위장형"),
    DELIVERY_SCAM             ("택배사기형"),
    INVESTMENT_SCAM           ("투자사기형"),
    CRYPTOCURRENCY_SCAM       ("가상화폐 사기형"),
    STOCK_INVESTMENT_SCAM     ("주식투자 사기형"),
    IPO_SCAM                  ("청약 공모주 사기형"),
    FALSE_PAYMENT_SCAM        ("허위결제사기형");

    private final String korName;

    FraudType(String korName) {
        this.korName = korName;
    }

}
