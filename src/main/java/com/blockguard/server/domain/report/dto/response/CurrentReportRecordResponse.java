package com.blockguard.server.domain.report.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentReportRecordResponse {
    private Long reportId;
    private int step;
    private String updatedAt;
}
