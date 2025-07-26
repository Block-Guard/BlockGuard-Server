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
public class GptResponse {
    private Double score;
    private String estimatedFraudType;
    private List<String> keywords;
    private String explanation;
}

