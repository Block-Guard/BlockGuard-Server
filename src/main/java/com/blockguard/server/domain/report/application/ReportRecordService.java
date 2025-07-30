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

        ReportStepProgress progress = ReportStepProgress.createWithDefaultCheckboxes(userReportRecord, ReportStep.STEP1);
        userReportRecord.addProgress(progress);
        userReportRecordRepository.save(userReportRecord);

        return ReportRecordResponse.from(userReportRecord, progress);
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
        UserReportRecord record = getUserReportRecord(user, reportId);
        ReportStep step = getReportStep(stepNumber);
        ReportStepProgress progress = getReportStepProgress(record, step);

        return buildReportRecordStepResponse(reportId, stepNumber, progress, record);
    }

    @Transactional
    public ReportRecordStepResponse updateStepInfo(User user, Long reportId, int stepNumber, UpdateReportStepRequest updateReportStepRequest) {
        UserReportRecord record = getUserReportRecord(user, reportId);
        ReportStep step = getReportStep(stepNumber);
        ReportStepProgress progress = getReportStepProgress(record, step);

        // 체스박스 수 검증
        validateCheckboxCounts(updateReportStepRequest, step);
        // 체크박스 상태 업데이트
        updateCheckboxStates(updateReportStepRequest, progress);

        // 현 단계가 완료되었을때 다음 단계 생성 또는 해당 신고 완료
        if (updateReportStepRequest.getIsCompleted()) {
            // step 완료 플레그 업데이트
            progress.setCompleted(true);
            createNextStepIfNeeded(stepNumber, record);
        }

        userReportRecordRepository.save(record);

        return buildReportRecordStepResponse(reportId, stepNumber, progress, record);
    }

    private void createNextStepIfNeeded(int stepNumber, UserReportRecord record) {
        // 마지막 단계가 아니라면
        if (stepNumber < ReportStep.STEP4.getStepNumber()){

            // 다음 스텝 존재 여부 검증
            ReportStep nextStep = ReportStep.from(stepNumber + 1).get();
            boolean exists = record.getProgressList().stream()
                    .anyMatch(p -> p.getStep() == nextStep);

            // 다음 스텝이 존재하지 않는다면 새롭게 생성
            if (!exists) {
                ReportStepProgress nextProgress = ReportStepProgress.createWithDefaultCheckboxes(record, nextStep);
                record.addProgress(nextProgress);
            }
        }
        // 마지막 STEP 이 완료된 것이라면
        else{
            record.setCompleted(true);
        }
    }

    private void updateCheckboxStates(UpdateReportStepRequest updateReportStepRequest, ReportStepProgress progress) {
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
    }

    private void validateCheckboxCounts(UpdateReportStepRequest request, ReportStep step) {
        ReportStepCheckboxConfig checkboxConfig = ReportStepCheckboxConfig.of(step);

        boolean isRequiredCountInvalid = request.getCheckBoxes().size() != checkboxConfig.getRequiredCount();
        boolean isRecommendedCountInvalid = checkboxConfig.getRecommendedCount() > 0 &&
                (request.getRecommendedCheckBoxes() == null ||
                        request.getRecommendedCheckBoxes().size() != checkboxConfig.getRecommendedCount());

        if (isRequiredCountInvalid || isRecommendedCountInvalid) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_CHECKBOX_COUNT);
        }
    }

    private ReportRecordStepResponse buildReportRecordStepResponse(Long reportId, int stepNumber, ReportStepProgress progress, UserReportRecord record) {
        List<Boolean> resultRequiredCheckboxes = getRequiredCheckboxes(progress, CheckboxType.REQUIRED);
        List<Boolean> resultRecommendedCheckboxes = getRequiredCheckboxes(progress, CheckboxType.RECOMMENDED);

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

    private List<Boolean> getRequiredCheckboxes(ReportStepProgress progress, CheckboxType required) {
        return progress.getCheckboxes().stream()
                .filter(cb -> cb.getType() == required)
                .sorted(Comparator.comparingInt(ReportStepCheckbox::getBoxIndex))
                .map(ReportStepCheckbox::isChecked)
                .toList();
    }

    private ReportStepProgress getReportStepProgress(UserReportRecord record, ReportStep step) {
        return record.getProgressList().stream()
                .filter(p -> p.getStep() == step)
                .findFirst()
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));
    }

    private ReportStep getReportStep(int stepNumber) {
        return ReportStep.from(stepNumber)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.INVALID_STEP));
    }

    private UserReportRecord getUserReportRecord(User user, Long reportId) {
        return userReportRecordRepository.findByIdAndUser(reportId, user)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.REPORT_NOT_FOUND));
    }
}
