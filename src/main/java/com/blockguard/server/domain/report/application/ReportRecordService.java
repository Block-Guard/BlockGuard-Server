package com.blockguard.server.domain.report.application;

import com.blockguard.server.domain.report.dao.UserReportRecordRepository;
import com.blockguard.server.domain.report.domain.ReportStepCheckbox;
import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.report.domain.enums.CheckboxType;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.blockguard.server.domain.report.domain.enums.ReportStepCheckboxConfig;
import com.blockguard.server.domain.report.dto.response.ReportRecordResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportRecordService {
    private final UserReportRecordRepository userReportRecordRepository;

    @Transactional
    public ReportRecordResponse createReportRecord(User user) {
        if (userReportRecordRepository.existsByUserAndIsCompletedFalse(user)) {
            throw new BusinessExceptionHandler(ErrorCode.REPORT_ALREADY_IN_PROGRESS);
        }

        UserReportRecord userReportRecord = UserReportRecord.builder()
                .user(user)
                .build();

        ReportStepProgress progress1 = ReportStepProgress.builder()
                .record(userReportRecord)
                .step(ReportStep.STEP1)
                .build();

        ReportStepCheckboxConfig cfg = ReportStepCheckboxConfig.of(ReportStep.STEP1);

        // STEP1 필수 체크박스 3개
        for (int i = 0; i < cfg.getRequiredCount(); i++) {
            progress1.getCheckboxes().add(ReportStepCheckbox.builder()
                    .stepProgress(progress1)
                    .type(CheckboxType.REQUIRED)
                    .boxIndex(i)
                    .isChecked(false)
                    .build());
        }

        // STEP1 권장 체크박스 2개
        for (int i = 0; i < cfg.getRecommendedCount(); i++) {
            progress1.getCheckboxes().add(ReportStepCheckbox.builder()
                    .stepProgress(progress1)
                    .type(CheckboxType.RECOMMENDED)
                    .boxIndex(i)
                    .isChecked(false)
                    .build());
        }

        userReportRecord.getProgressList().add(progress1);
        userReportRecordRepository.save(userReportRecord);

        return ReportRecordResponse.from(userReportRecord, progress1);
    }
}
