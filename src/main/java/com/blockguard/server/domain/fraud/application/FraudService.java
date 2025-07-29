package com.blockguard.server.domain.fraud.application;

import com.blockguard.server.domain.fraud.dao.FraudUrlRepository;
import com.blockguard.server.domain.fraud.domain.FraudUrl;
import com.blockguard.server.domain.fraud.domain.enums.RiskLevel;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.fraud.dto.response.FraudUrlResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FraudService {
    private final FraudUrlRepository fraudUrlRepository;

    public FraudUrlResponse checkFraudUrl(FraudUrlRequest fraudUrlRequest) {
        String url = fraudUrlRequest.getUrl();
        Optional<FraudUrl> foundUrl = fraudUrlRepository.findByUrl(url);

        if(foundUrl.isPresent()){
            FraudUrl fraudUrl = foundUrl.get();
            return FraudUrlResponse.builder()
                    .riskLevel(RiskLevel.Danger)
                    .build();
        }

        return FraudUrlResponse.builder()
                .riskLevel(RiskLevel.Safety)
                .build();
    }
}
