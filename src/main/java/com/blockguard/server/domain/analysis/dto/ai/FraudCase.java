package com.blockguard.server.domain.analysis.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FraudCase {

    @JsonProperty("message_content")
    private String messageContent;

    private String description;
}