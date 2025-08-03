package com.blockguard.server.domain.fraud.application;

import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.fraud.dao.FraudUrlRepository;
import com.blockguard.server.domain.fraud.dto.request.FraudPhoneNumberRequest;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.fraud.dto.response.FraudRiskLevelResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import com.blockguard.server.infra.google.GoogleSafeBrowsingClient;
import com.blockguard.server.infra.number.FraudNumberClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FraudService {
    private final FraudUrlRepository fraudUrlRepository;
    private final GoogleSafeBrowsingClient googleSafeBrowsingService;
    private final FraudNumberClient fraudNumberClient;

    public FraudRiskLevelResponse checkFraudUrl(FraudUrlRequest fraudUrlRequest) {
        String url = fraudUrlRequest.getUrl();

        if (url == null || url.trim().isEmpty()){
            throw new BusinessExceptionHandler(ErrorCode.URL_REQUIRED);
        }

        // 1차: DB 검사
        if(fraudUrlRepository.existsByUrl(url)){
            return FraudRiskLevelResponse.builder()
                    .riskLevel(RiskLevel.Dangers)
                    .build();
        }

        // 2차: Google Safe Browsing API 호출
        boolean isSafe = googleSafeBrowsingService.isUrlSafe(url);
        if (!isSafe){
            return FraudRiskLevelResponse.builder()
                    .riskLevel(RiskLevel.Dangers)
                    .build();
        }

        return FraudRiskLevelResponse.builder()
                .riskLevel(RiskLevel.Safety)
                .build();
    }

    public FraudRiskLevelResponse checkFraudPhoneNumber(FraudPhoneNumberRequest fraudPhoneNumberRequest) {
        String phoneNumber = fraudPhoneNumberRequest.getPhoneNumber().replaceAll("\\s+", "");

        // 사기 전화번호 조회 api 호출
        String spamCount = fraudNumberClient.checkSpamNumber(phoneNumber).getSpamCount();

        if (isFraudNumber(spamCount)){
            return FraudRiskLevelResponse.builder()
                    .riskLevel(RiskLevel.Dangers)
                    .build();
        }

        return FraudRiskLevelResponse.builder()
                .riskLevel(RiskLevel.Safety)
                .build();
    }

    private Boolean isFraudNumber(String spamCount) {
        // spam count 최대 "1000+" 가 나오는 경우 고려
        return Integer.parseInt(spamCount.replaceAll("\\D+", "")) > 0;
    }
}
