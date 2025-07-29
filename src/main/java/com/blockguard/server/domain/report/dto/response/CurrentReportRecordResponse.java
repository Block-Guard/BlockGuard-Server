package com.blockguard.server.domain.report.dto.response;

import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentReportRecordResponse {
    private Long reportId;
    private int step;
    private String createdAt;

    public static CurrentReportRecordResponse from(UserReportRecord record, ReportStepProgress reportStepProgress) {
        return CurrentReportRecordResponse.builder()
                .reportId(record.getId())
                .step(reportStepProgress.getStep().ordinal() + 1)
                .createdAt(record.getCreatedAt().toString())
                .build();
    }
}
