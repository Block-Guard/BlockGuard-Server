package com.blockguard.server.domain.report.domain.enums;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.Getter;

@Getter
public enum ReportStepCheckboxConfig {
    STEP1(ReportStep.STEP1, 3, 2),
    STEP2(ReportStep.STEP2, 1, 0),
    STEP3(ReportStep.STEP3, 2, 0),
    STEP4(ReportStep.STEP4, 2, 3);

    private final ReportStep step;
    private final int requiredCount;
    private final int recommendedCount;

    ReportStepCheckboxConfig(ReportStep step, int requiredCount, int recommendedCount) {
        this.step = step;
        this.requiredCount = requiredCount;
        this.recommendedCount = recommendedCount;
    }

    public static ReportStepCheckboxConfig of(ReportStep step) {
        for (ReportStepCheckboxConfig cfg : values()) {
            if (cfg.getStep() == step) {
                return cfg;
            }
        }
        throw new BusinessExceptionHandler(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
