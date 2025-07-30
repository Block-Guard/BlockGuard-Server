package com.blockguard.server.domain.report.dto.response;

import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties("completed")
public class ReportRecordStepResponse {
    private Long reportId;
    private int step;
    private List<Boolean> checkBoxes;
    private List<Boolean> recommendedCheckBoxes;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private String createdAt;
}
