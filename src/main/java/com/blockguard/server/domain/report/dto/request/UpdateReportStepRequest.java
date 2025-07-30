package com.blockguard.server.domain.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UpdateReportStepRequest {
    @Schema(description = "필수 체크박스 상태 목록", example = "[true, false, true]")
    @NotNull
    @NotEmpty
    private List<Boolean> checkBoxes;

    @Schema(description = "권장 체크박스 상태 목록", example = "[true, false]")
    private List<Boolean> recommendedCheckBoxes;

    @Schema(description = "단계 완료 여부", example = "false")
    @NotNull
    private Boolean isCompleted;
}
