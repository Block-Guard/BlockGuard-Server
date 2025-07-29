package com.blockguard.server.domain.analysis.domain.enums;

import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonValue;
 
import lombok.AllArgsConstructor;
  
@Getter
public enum RiskLevel {
    Dangers("위험"),
    Caution("주의"),
    Safety("안전");

    private final String name;

    @JsonValue
    public String getName(){
      return name;
    }

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
