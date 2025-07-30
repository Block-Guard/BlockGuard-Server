package com.blockguard.server.domain.report.application;

import com.blockguard.server.domain.report.dao.UserReportRecordRepository;
import com.blockguard.server.domain.report.domain.ReportStepCheckbox;
import com.blockguard.server.domain.report.domain.ReportStepProgress;
import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.report.domain.enums.CheckboxType;
import com.blockguard.server.domain.report.domain.enums.ReportStep;
import com.blockguard.server.domain.report.domain.enums.ReportStepCheckboxConfig;
import com.blockguard.server.domain.report.dto.request.UpdateReportStepRequest;
import com.blockguard.server.domain.report.dto.response.CurrentReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordStepResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
                    .build());
        }

        // STEP1 권장 체크박스 2개
        for (int i = 0; i < cfg.getRecommendedCount(); i++) {
            progress1.getCheckboxes().add(ReportStepCheckbox.builder()
                    .stepProgress(progress1)
                    .type(CheckboxType.RECOMMENDED)
                    .boxIndex(i)
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
                .isCompleted(progress.isCompleted())
                .createdAt(String.valueOf(record.getCreatedAt()))
                .build();
    }

    @Transactional
    public ReportRecordStepResponse updateStepInfo(User user, Long reportId, int stepNumber, UpdateReportStepRequest updateReportStepRequest) {
        UserReportRecord record = userReportRecordRepository.findByIdAndUser(reportId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.REPORT_NOT_FOUND));

        ReportStep step = ReportStep.from(stepNumber)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));

        ReportStepProgress progress = record.getProgressList().stream()
                .filter(p -> p.getStep() == step)
                .findFirst()
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));

        // 체스박스 수 검증
        ReportStepCheckboxConfig checkboxConfig = ReportStepCheckboxConfig.of(step);

        if ((updateReportStepRequest.getCheckBoxes().size() != checkboxConfig.getRequiredCount()) ||
        (updateReportStepRequest.getRecommendedCheckBoxes() == null && checkboxConfig.getRecommendedCount() > 0) ||
                (updateReportStepRequest.getRecommendedCheckBoxes() != null && (updateReportStepRequest.getRecommendedCheckBoxes().size() != checkboxConfig.getRecommendedCount()))
        ) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_CHECKBOX_COUNT);
        }

        // 체크박스 상태 업데이트
        progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == CheckboxType.REQUIRED)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .forEach((cb) ->
                        cb.updateChecked(updateReportStepRequest.getCheckBoxes().get(cb.getBoxIndex()))
                );

        if(updateReportStepRequest.getRecommendedCheckBoxes() != null){
            progress.getCheckboxes().stream()
                    .filter(cb -> cb.getType() == CheckboxType.RECOMMENDED)
                    .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                    .forEach((cb) ->
                            cb.updateChecked(updateReportStepRequest.getRecommendedCheckBoxes().get(cb.getBoxIndex()))
                    );
        }

        // step 완료 플레그 업데이트
        progress.setCompleted(updateReportStepRequest.getIsCompleted());

        // 현 단계가 완료되었을때 다음 단계 생성 또는 해당 신고 완료
        if (updateReportStepRequest.getIsCompleted()) {
            // 마지막 단계가 아니라면
            if (stepNumber < ReportStep.STEP4.getStepNumber()){

                // 다음 스텝 존재 여부 검증
                ReportStep nextStep = ReportStep.from(stepNumber + 1).get();
                boolean exists = record.getProgressList().stream()
                        .anyMatch(p -> p.getStep() == nextStep);

                // 다음 스텝이 존재하지 않는다면 새롭게 생성
                if (!exists) {
                    ReportStepProgress nextProgress = ReportStepProgress.builder()
                            .record(record)
                            .step(nextStep)
                            .build();

                    ReportStepCheckboxConfig nextStepConfig = ReportStepCheckboxConfig.of(nextStep);

                    for (int i = 0; i < nextStepConfig.getRequiredCount(); i++) {
                        nextProgress.getCheckboxes().add(ReportStepCheckbox.builder()
                                .stepProgress(nextProgress)
                                .type(CheckboxType.REQUIRED)
                                .boxIndex(i)
                                .build());
                    }

                    // STEP1 권장 체크박스 2개
                    for (int i = 0; i < nextStepConfig.getRecommendedCount(); i++) {
                        nextProgress.getCheckboxes().add(ReportStepCheckbox.builder()
                                .stepProgress(nextProgress)
                                .type(CheckboxType.RECOMMENDED)
                                .boxIndex(i)
                                .build());
                    }
                    record.addProgress(nextProgress);
                }
            }
            // 마지막 STEP이 완료된 것이라면
            else{
                record.setCompleted(true);
            }
        }

        userReportRecordRepository.save(record);

        List<Boolean> resultRequiredCheckboxes = progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == CheckboxType.REQUIRED)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .map(ReportStepCheckbox::isChecked)
                .toList();

        List<Boolean> resultRecommendedCheckboxes = progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == CheckboxType.RECOMMENDED)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .map(ReportStepCheckbox::isChecked)
                .toList();

        resultRecommendedCheckboxes = resultRecommendedCheckboxes.isEmpty() ? null : resultRecommendedCheckboxes;

        return ReportRecordStepResponse.builder()
                .reportId(reportId)
                .step(stepNumber)
                .checkBoxes(resultRequiredCheckboxes)
                .recommendedCheckBoxes(resultRecommendedCheckboxes)
                .createdAt(String.valueOf(record.getCreatedAt()))
                .isCompleted(progress.isCompleted())
                .build();
    }
}
