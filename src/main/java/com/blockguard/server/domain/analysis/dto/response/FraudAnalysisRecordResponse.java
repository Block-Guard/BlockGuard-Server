package com.blockguard.server.domain.analysis.dto.response;

import com.blockguard.server.domain.analysis.domain.FraudAnalysisRecord;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FraudAnalysisRecordResponse {
    private Long fraudAnalysisRecordId;
    private String estimatedFraudType;
    private String riskLevel;
    private LocalDateTime analyzedAt;

    public static FraudAnalysisRecordResponse from(FraudAnalysisRecord record){
        return FraudAnalysisRecordResponse.builder()
                .fraudAnalysisRecordId(record.getId())
                .analyzedAt(record.getCreatedAt())
                .estimatedFraudType(record.getEstimatedFraudType() != null ? record.getEstimatedFraudType().getKorName(): null)
                .riskLevel(record.getRiskLevel().getValue())
                .build();
    }
}
