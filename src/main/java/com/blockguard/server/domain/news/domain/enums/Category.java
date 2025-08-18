package com.blockguard.server.domain.news.domain.enums;

public enum Category {
    VOICE_PHISHING, SMISHING, MESSAGE_VOICE_PHISHING, ETC,
    LOAN_FRAUD,              // 대출 사기
    CARD_IMPERSONATION,      // 카드사 사칭
    FAMILY_IMPERSONATION,    // 가족/지인 사칭
    EVENT_IMPERSONATION,     // 경조사 사칭
    PUBLIC_IMPERSONATION,    // 공공기관 사칭
    PARTTIME_SCAM,           // 알바/부업 사기
    GOVT_SUBSIDY_FAKE,       // 정부지원금 위장
    PARCEL_SCAM,             // 택배 사기
    INVESTMENT_SCAM,         // 투자 사기
    FAKE_PAYMENT;            // 허위결제 사기

    public static Category from(String input) {
        return switch (input.trim()) {
            case "보이스피싱", "보이스 피싱" -> VOICE_PHISHING;
            case "스미싱" -> SMISHING;
            case "메신저 피싱", "메신저피싱" -> MESSAGE_VOICE_PHISHING;

            case "대출 사기형" -> LOAN_FRAUD;
            case "카드사 사칭형" -> CARD_IMPERSONATION;
            case "가족/지인 사칭형" -> FAMILY_IMPERSONATION;
            case "경조사 사칭형" -> EVENT_IMPERSONATION;
            case "공공기관 사칭형" -> PUBLIC_IMPERSONATION;
            case "알바/부업 사기형" -> PARTTIME_SCAM;
            case "정부지원금 위장형" -> GOVT_SUBSIDY_FAKE;
            case "택배 사기형" -> PARCEL_SCAM;
            case "투자 사기형" -> INVESTMENT_SCAM;
            case "허위결제 사기형" -> FAKE_PAYMENT;
            default -> ETC;
        };
    }
}
