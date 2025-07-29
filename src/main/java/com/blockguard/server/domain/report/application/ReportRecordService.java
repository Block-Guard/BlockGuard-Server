package com.blockguard.server.domain.report.application;

import com.blockguard.server.domain.report.dao.UserReportRecordRepository;
import com.blockguard.server.domain.report.domain.ReportStepCheckbox;
import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.report.domain.enums.CheckboxType;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.blockguard.server.domain.report.domain.enums.ReportStepCheckboxConfig;
import com.blockguard.server.domain.report.dto.response.CurrentReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordStepResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    public CurrentReportRecordResponse getCurrentRecord(User user) {
        Optional<CurrentReportRecordResponse> currentReportRecordResponse = userReportRecordRepository.findFirstByUserAndIsCompletedFalseOrderByCreatedAtDesc(user)
               .map(record -> {
                    ReportStepProgress lastProgress = record.getProgressList().stream()
                            .max(Comparator.comparing(ReportStepProgress::getStep))
                            .orElseThrow();
                    int stepNum = lastProgress.getStep().getStepNumber();
                    return CurrentReportRecordResponse.builder()
                            .reportId(record.getId())
                            .createdAt(String.valueOf(record.getCreatedAt()))
                            .step(stepNum)
                            .build();

                });
        return currentReportRecordResponse.orElse(null);
    }

    @Transactional
    public ReportRecordStepResponse getStepInfo(User user, Long reportId, int stepNumber) {
        UserReportRecord record = userReportRecordRepository.findByIdAndUser(reportId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.REPORT_NOT_FOUND));

        ReportStep step = ReportStep.from(stepNumber)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));

        ReportStepProgress progress = record.getProgressList().stream()
                .filter(p -> p.getStep() == step)
                .findFirst()
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));

        List<Boolean> requiredCheckboxes = progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == CheckboxType.REQUIRED)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .map(ReportStepCheckbox::isChecked)
                .toList();

        List<Boolean> recommendedCheckboxes = progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == CheckboxType.RECOMMENDED)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .map(ReportStepCheckbox::isChecked)
                .toList();

       recommendedCheckboxes = recommendedCheckboxes.isEmpty() ? null : recommendedCheckboxes;

        return ReportRecordStepResponse.builder()
                .reportId(reportId)
                .step(stepNumber)
                .checkBoxes(requiredCheckboxes)
                .recommendedCheckBoxes(recommendedCheckboxes)
                .createdAt(String.valueOf(record.getCreatedAt()))
                .build();
    }
}
