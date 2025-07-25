package com.blockguard.server.domain.analysis.api;

import com.blockguard.server.domain.analysis.application.FraudAnalysisService;
import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class FraudAnalysisApi {

    private final FraudAnalysisService fraudAnalysisService;

    @PostMapping("/fraud-analysis")
    @Operation(summary = "사기 분석")
    public ResponseEntity<BaseResponse<FraudAnalysisResponse>> analyzeFraud(@ModelAttribute FraudAnalysisRequest fraudAnalysisRequest) {
        FraudAnalysisResponse fraudAnalysisResponse = fraudAnalysisService.fraudAnalysis(fraudAnalysisRequest);
        return ResponseEntity.ok(BaseResponse.of(SuccessCode.ANALYZE_FRAUD_SUCCESS, fraudAnalysisResponse));
    }
}
