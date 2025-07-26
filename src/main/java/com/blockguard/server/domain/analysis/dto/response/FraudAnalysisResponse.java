package com.blockguard.server.domain.analysis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FraudAnalysisResponse {
    // for test
    private String messageContent;
    private List<String> keywords;
    private String additionalDescription;
    private String imageContent;
}
