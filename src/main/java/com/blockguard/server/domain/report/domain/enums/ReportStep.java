package com.blockguard.server.domain.report.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ReportStep {
    STEP1(1),
    STEP2(2),
    STEP3(3),
    STEP4(4);

    private final int stepNumber;

    ReportStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public static Optional<ReportStep> from(int v) {
        return Arrays.stream(values()).filter(s -> s.stepNumber == v).findFirst();
    }

}
