package com.blockguard.server.domain.report.api;

import com.blockguard.server.domain.report.application.ReportRecordService;
import com.blockguard.server.domain.report.dto.response.CurrentReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordResponse;
import com.blockguard.server.domain.report.dto.response.ReportRecordStepResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.resolver.CurrentUser;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report-records")
public class ReportRecordApi {
    private final ReportRecordService reportRecordService;

    @Operation(summary = "신고 현황 생성 API")
    @CustomExceptionDescription(SwaggerResponseDescription.CREATE_REPORT_FAIL)
    @PostMapping
    public BaseResponse<ReportRecordResponse> createReportRecord(@Parameter(hidden = true) @CurrentUser User user) {
        ReportRecordResponse reportRecordResponse = reportRecordService.createReportRecord(user);
        return BaseResponse.of(SuccessCode.CREATE_REPORT_RECORD_SUCCESS, reportRecordResponse);
    }

    @Operation(summary = "진행중인 신고 현황 조회 API")
    @CustomExceptionDescription(SwaggerResponseDescription.INVALID_TOKEN)
    @GetMapping("/current")
    public BaseResponse<CurrentReportRecordResponse> getCurrentRecord(@Parameter(hidden = true) @CurrentUser User user) {
        CurrentReportRecordResponse currentReportRecordResponse = reportRecordService.getCurrentRecord(user);
        return BaseResponse.of(SuccessCode.CURRENT_REPORT_FOUND, currentReportRecordResponse);
    }

    @Operation(summary = "신고 현황의 특정 단계 정보 조회 API")
    @CustomExceptionDescription(SwaggerResponseDescription.INVALID_TOKEN)
    @GetMapping("/{reportId}/steps/{stepNumber}")
    public BaseResponse<ReportRecordStepResponse> getStepInfo(
            @Parameter(hidden = true) @CurrentUser User user,
            @PathVariable Long reportId,
            @PathVariable int stepNumber
    ) {
        ReportRecordStepResponse reportRecordStepResponse = reportRecordService.getStepInfo(user, reportId, stepNumber);
        return BaseResponse.of(SuccessCode.STEP_INFO_FOUND, reportRecordStepResponse);
    }
}
