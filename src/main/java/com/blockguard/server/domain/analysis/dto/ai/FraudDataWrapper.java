package com.blockguard.server.domain.analysis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FraudDataWrapper {

    @JsonProperty("fraud_examples")
    private List<FraudExample> fraudExamples;
}