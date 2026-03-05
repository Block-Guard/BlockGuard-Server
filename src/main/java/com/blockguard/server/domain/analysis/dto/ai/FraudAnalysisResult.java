package com.blockguard.server.domain.analysis.dto.ai;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FraudAnalysisResult {

    private String fraudType;
    private int riskScore;
    private String reason;
    private String recommendation;
}