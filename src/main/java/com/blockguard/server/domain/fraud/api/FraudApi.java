package com.blockguard.server.domain.fraud.api;

import com.blockguard.server.domain.fraud.application.FraudService;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.fraud.dto.response.FraudUrlResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    public BaseResponse<FraudUrlResponse> fraudUrl(@RequestBody FraudUrlRequest fraudUrlRequest){
        FraudUrlResponse fraudUrlResponse = fraudService.checkFraudUrl(fraudUrlRequest);
        return BaseResponse.of(SuccessCode.CHECK_URL_FRAUD_SUCCESS, fraudUrlResponse);
    }

}
