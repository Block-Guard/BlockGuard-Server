package com.blockguard.server.domain.report.dto.response;

import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties("completed")
public class ReportRecordResponse {
    private Long reportId;
    private int step;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private String createdAt;

    public static ReportRecordResponse from(UserReportRecord record, ReportStepProgress reportStepProgress) {
        return ReportRecordResponse.builder()
                .reportId(record.getId())
                .step(ReportStep.getOrder(reportStepProgress.getStep()))
                .isCompleted(record.isCompleted())
                .createdAt(record.getCreatedAt().toString())
                .build();
    }
}
