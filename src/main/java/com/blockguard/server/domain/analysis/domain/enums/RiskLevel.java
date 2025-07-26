package com.blockguard.server.domain.analysis.domain.enums;

import lombok.Getter;

@Getter
public enum RiskLevel {
    Dangers("위험"),
    Caution("주의"),
    Safety("안전");

    private final String name;

    RiskLevel(String name) {
        this.name = name;
    }

    public static RiskLevel fromScore(double score) {
        if (score <= 30) {
            return Safety;
        } else if (score <= 60) {
            return Caution;
        } else {
            return Dangers;
        }
    }

}
