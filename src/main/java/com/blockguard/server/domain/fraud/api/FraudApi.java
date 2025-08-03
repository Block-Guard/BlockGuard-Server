package com.blockguard.server.domain.fraud.api;

import com.blockguard.server.domain.fraud.application.FraudService;
import com.blockguard.server.domain.fraud.dto.request.FraudPhoneNumberRequest;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.fraud.dto.response.FraudRiskLevelResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/fraud")
public class FraudApi {
    private final FraudService fraudService;

    @PostMapping("/url")
    @Operation(summary = "입력된 url 사기 분석")
    public BaseResponse<FraudRiskLevelResponse> fraudUrl(@Valid @RequestBody FraudUrlRequest fraudUrlRequest){
        FraudRiskLevelResponse fraudRiskLevelResponse = fraudService.checkFraudUrl(fraudUrlRequest);
        return BaseResponse.of(SuccessCode.CHECK_URL_FRAUD_SUCCESS, fraudRiskLevelResponse);
    }

    @PostMapping("/number")
    @CustomExceptionDescription(SwaggerResponseDescription.CHECK_FRAUD_PHONE_NUMBER_FAIL)
    @Operation(summary = "입력된 전화번호 사기 분석", description = "전화번호의 입력 형식 상관없음")
    public BaseResponse<FraudRiskLevelResponse> fraudPhoneNumber(@Valid @RequestBody FraudPhoneNumberRequest fraudPhoneNumberRequest){
        FraudRiskLevelResponse fraudRiskLevelResponse = fraudService.checkFraudPhoneNumber(fraudPhoneNumberRequest);
        return BaseResponse.of(SuccessCode.CHECK_PHONE_NUMBER_FRAUD_SUCCESS, fraudRiskLevelResponse);
    }

}
