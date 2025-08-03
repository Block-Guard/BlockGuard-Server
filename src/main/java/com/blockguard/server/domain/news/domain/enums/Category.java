package com.blockguard.server.domain.news.domain.enums;

public enum Category {
    VOICE_PHISHING, SMISHING, MESSAGE_VOICE_PHISHING, ETC;

    public static Category from(String input){
        return switch (input){
            case "보이스피싱" -> VOICE_PHISHING;
            case "스미싱" -> SMISHING;
            case "메신저 피싱", "메신저피싱" -> MESSAGE_VOICE_PHISHING;
            default -> ETC;
        };
    }
}
