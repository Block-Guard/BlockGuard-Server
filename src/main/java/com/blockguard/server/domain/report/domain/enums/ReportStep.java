package com.blockguard.server.domain.report.domain.enums;

public enum ReportStep {
    STEP1,
    STEP2,
    STEP3,
    STEP4;

    public static int getOrder(ReportStep step) {
        return step.ordinal() + 1;
    }
}
