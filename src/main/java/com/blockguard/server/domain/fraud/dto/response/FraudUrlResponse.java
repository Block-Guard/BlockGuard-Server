package com.blockguard.server.domain.fraud.dto.response;

import com.blockguard.server.domain.fraud.domain.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudUrlResponse {
    private String url;
    private RiskLevel riskLevel;
}
