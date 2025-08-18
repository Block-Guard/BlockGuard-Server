package com.blockguard.server.domain.news.domain.enums;

import java.util.Map;
import java.util.Optional;

public enum Category {
    VOICE_PHISHING, SMISHING, MESSAGE_VOICE_PHISHING, ETC,
    INSTITUTION_IMPERSONATION, // 기관 사칭
    LOAN_FRAUD,              // 대출 사기
    CARD_IMPERSONATION,      // 카드사 사칭
    FAMILY_IMPERSONATION,    // 가족/지인 사칭
    EVENT_IMPERSONATION,     // 경조사 사칭
    PUBLIC_IMPERSONATION,    // 공공기관 사칭 (범칙금/과태로 부과, 건강보험공단 사칭형, 경찰출석요구형 ,국세청 사칭형)
    PART_TIME_SCAM,           // 알바/부업 사기
    GOVERNMENT_GRANT_SCAM,       // 정부지원금 위장
    DELIVERY_SCAM,             // 택배 사기
    INVESTMENT_SCAM,         // 투자 사기
    FALSE_PAYMENT_SCAM;            // 허위결제 사기

    private static final Map<String, Category> MAPPING = Map.ofEntries(
            Map.entry("보이스피싱", VOICE_PHISHING),
            Map.entry("보이스 피싱", VOICE_PHISHING),
            Map.entry("스미싱", SMISHING),
            Map.entry("메신저피싱", MESSAGE_VOICE_PHISHING),
            Map.entry("메신저 피싱", MESSAGE_VOICE_PHISHING),
            Map.entry("기관 사칭형", INSTITUTION_IMPERSONATION),
            Map.entry("대출 사기형", LOAN_FRAUD),
            Map.entry("카드사 사칭형", CARD_IMPERSONATION),
            Map.entry("가족/지인 사칭형", FAMILY_IMPERSONATION),
            Map.entry("경조사 사칭형", EVENT_IMPERSONATION),
            Map.entry("공공기관 사칭형", PUBLIC_IMPERSONATION),
            Map.entry("알바/부업 사기형", PART_TIME_SCAM),
            Map.entry("정부지원금 위장형", GOVERNMENT_GRANT_SCAM),
            Map.entry("택배 사기형", DELIVERY_SCAM),
            Map.entry("투자 사기형", INVESTMENT_SCAM),
            Map.entry("허위결제 사기형", FALSE_PAYMENT_SCAM),

            Map.entry("돌잔치 초대장형", EVENT_IMPERSONATION),
            Map.entry("모바일 청첩장형", EVENT_IMPERSONATION),
            Map.entry("부고문자형", EVENT_IMPERSONATION),
            Map.entry("범칙금/과태료 부과형", PUBLIC_IMPERSONATION),
            Map.entry("건강보험공단 사칭형", PUBLIC_IMPERSONATION),
            Map.entry("경찰출석요구형", PUBLIC_IMPERSONATION),
            Map.entry("국세청 사칭형", PUBLIC_IMPERSONATION),
            Map.entry("가상화폐 사기형", INVESTMENT_SCAM),
            Map.entry("주식투자 사기형", INVESTMENT_SCAM),
            Map.entry("청약 공모주 사기형", INVESTMENT_SCAM)
    );

    public static Category from(String input) {
        return Optional.ofNullable(MAPPING.get(input.trim()))
                .orElse(ETC);
    }
        /*return switch (input.trim()) {
            case "보이스피싱", "보이스 피싱" -> VOICE_PHISHING;
            case "스미싱" -> SMISHING;
            case "메신저 피싱", "메신저피싱" -> MESSAGE_VOICE_PHISHING;

            case "기관 사칭형" -> INSTITUTION_IMPERSONATION;
            case "대출 사기형" -> LOAN_FRAUD;
            case "카드사 사칭형" -> CARD_IMPERSONATION;
            case "가족/지인 사칭형" -> FAMILY_IMPERSONATION;
            case "경조사 사칭형" -> EVENT_IMPERSONATION;
            case "공공기관 사칭형" -> PUBLIC_IMPERSONATION;
            case "알바/부업 사기형" -> PART_TIME_SCAM;
            case "정부지원금 위장형" -> GOVERNMENT_GRANT_SCAM;
            case "택배 사기형" -> DELIVERY_SCAM;
            case "투자 사기형" -> INVESTMENT_SCAM;
            case "허위결제 사기형" -> FALSE_PAYMENT_SCAM;
            default -> ETC;
        };*/

}
