package com.blockguard.server.domain.report.dto.response;

import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportRecordStepResponse {
    private Long reportId;
    private int step;
    private List<Boolean> checkBoxes;
    private List<Boolean> recommendedCheckBoxes;
    private boolean isCompleted;
    private String createdAt;
}
