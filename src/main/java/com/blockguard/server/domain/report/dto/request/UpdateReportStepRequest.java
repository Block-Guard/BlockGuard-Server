package com.blockguard.server.domain.report.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateReportStepRequest {
    @NotNull
    private List<Boolean> checkBoxes;

    private List<Boolean> recommendedCheckBoxes;

    @NotNull
    private Boolean isCompleted;
}
