package com.blockguard.server.domain.analysis.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RiskLevel {
    Dangers("위험"), Caution("주의"), Safety("안전");


    private final String value;

    @JsonValue
    public String getValue(){
        return value;
    }
}
