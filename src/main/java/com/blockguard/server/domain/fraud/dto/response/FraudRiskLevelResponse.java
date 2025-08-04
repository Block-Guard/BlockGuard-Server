package com.blockguard.server.domain.fraud.dto.response;

import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FraudRiskLevelResponse {
    private RiskLevel riskLevel;
}
