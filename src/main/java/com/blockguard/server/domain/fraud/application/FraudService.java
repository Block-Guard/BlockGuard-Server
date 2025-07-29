package com.blockguard.server.domain.fraud.application;

import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.fraud.dao.FraudUrlRepository;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.fraud.dto.response.FraudUrlResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import com.blockguard.server.infra.google.GoogleSafeBrowsingClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FraudService {
    private final FraudUrlRepository fraudUrlRepository;
    private final GoogleSafeBrowsingClient googleSafeBrowsingService;

    public FraudUrlResponse checkFraudUrl(FraudUrlRequest fraudUrlRequest) {
        String url = fraudUrlRequest.getUrl();

        if (url == null || url.trim().isEmpty()){
            throw new BusinessExceptionHandler(ErrorCode.URL_REQUIRED);
        }

        // 1차: DB 검사
        if(fraudUrlRepository.existsByUrl(url)){
            return FraudUrlResponse.builder()
                    .riskLevel(RiskLevel.Dangers)
                    .build();
        }

        // 2차: Google Safe Browsing API 호출
        boolean isSafe = googleSafeBrowsingService.isUrlSafe(url);
        if (!isSafe){
            return FraudUrlResponse.builder()
                    .riskLevel(RiskLevel.Dangers)
                    .build();
        }

        return FraudUrlResponse.builder()
                .riskLevel(RiskLevel.Safety)
                .build();
    }
}
