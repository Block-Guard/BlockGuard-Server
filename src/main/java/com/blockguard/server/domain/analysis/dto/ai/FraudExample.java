package com.blockguard.server.domain.analysis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FraudExample {

    @JsonProperty("type_name")
    private String typeName;

    private List<String> keywords;

    private List<FraudCase> cases;
}