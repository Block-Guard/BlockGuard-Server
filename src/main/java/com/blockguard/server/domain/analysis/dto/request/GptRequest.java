package com.blockguard.server.domain.analysis.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GptRequest {
    private String messageContent;
    private List<String> keywords;
    private String additionalDescription;
    private String imageContent;
}
