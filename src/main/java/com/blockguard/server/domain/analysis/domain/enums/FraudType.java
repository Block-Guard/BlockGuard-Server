package com.blockguard.server.domain.analysis.domain.enums;

import lombok.Getter;

@Getter
public enum FraudType {
    INSTITUTION_IMPERSONATION("보이스피싱_기관사칭형"),
    LOAN_FRAUD               ("보이스피싱_대출사기형"),
    CARD_COMPANY_IMPERSONATION("보이스피싱_카드사 사칭형"),
    FAMILY_IMPERSONATION      ("메신저피싱_가족/지인사칭형"),
    FIRST_BIRTHDAY_INVITE     ("스미싱_돌잔치 초대장형"),
    MOBILE_WEDDING_INVITE     ("스미싱_모바일 청첩장형"),
    OBITUARY_MESSAGE          ("스미싱_부고문자형"),
    FINE_AND_PENALTY          ("스미싱_범칙금/과태료 부과형"),
    HEALTH_INSURANCE_IMPERSONATION("스미싱_건강보험공단 사칭형"),
    POLICE_SUMMONS            ("스미싱_경찰출석요구형"),
    NATIONAL_TAX_SERVICE      ("스미싱_국세청 사칭형"),
    PART_TIME_SCAM            ("스미싱_알바/부업 사기형"),
    GOVERNMENT_GRANT_SCAM     ("스미싱_정부지원금 위장형"),
    DELIVERY_SCAM             ("스미싱_택배사기형"),
    CRYPTOCURRENCY_SCAM       ("스미싱_가상화폐 사기형"),
    STOCK_INVESTMENT_SCAM     ("스미싱_주식투자 사기형"),
    IPO_SCAM                  ("스미싱_청약 공모주 사기형"),
    FALSE_PAYMENT_SCAM        ("스미싱_허위결제사기형");

    private final String korName;

    FraudType(String korName) {
        this.korName = korName;
    }

}
