package com.blockguard.server.domain.analysis.api;

import com.blockguard.server.domain.analysis.application.FraudAnalysisService;
import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisRecordResponse;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.resolver.CurrentUser;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class FraudAnalysisApi {

    private final FraudAnalysisService fraudAnalysisService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/fraud-analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "사기 분석")
    public BaseResponse<FraudAnalysisResponse> analyzeFraud
            (@RequestParam("fraudAnalysisRequest") String jsonStr,
             @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles
            ) throws JsonProcessingException {

        // TODO: 이미지 파일 개수 픽스 필요
        if (imageFiles != null && imageFiles.size() > 2) {
            throw new BusinessExceptionHandler(ErrorCode.IMAGE_UPLOAD_LIMIT_EXCEEDED);
        }

        FraudAnalysisRequest request = objectMapper.readValue(jsonStr, FraudAnalysisRequest.class);
        FraudAnalysisResponse fraudAnalysisResponse = fraudAnalysisService.fraudAnalysis(request, imageFiles);
        return BaseResponse.of(SuccessCode.ANALYZE_FRAUD_SUCCESS, fraudAnalysisResponse);
    }

    @GetMapping(value = "/fraud-analysis")
    @Operation(summary = "사기 분석 기록 조회")
    public BaseResponse<List<FraudAnalysisRecordResponse>> getAnalyzeFraudList(@Parameter(hidden = true) @CurrentUser User user){
        List<FraudAnalysisRecordResponse> fraudAnalysisRecordResponse = fraudAnalysisService.getAnalyzeFraudList(user);
        return BaseResponse.of(SuccessCode.GET_ANALYZE_FRAUD_LIST_SUCCESS, fraudAnalysisRecordResponse);
    }
}
