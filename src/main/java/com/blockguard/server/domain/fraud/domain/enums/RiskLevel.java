package com.blockguard.server.domain.fraud.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RiskLevel {
    Danger("위험"),
    Safety("안전");

    private final String value;

    @JsonValue
    public String getValue(){
        return value;
    }
}
