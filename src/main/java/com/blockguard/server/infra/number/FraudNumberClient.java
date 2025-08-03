package com.blockguard.server.infra.number;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudNumberClient {
    private final RestTemplate restTemplate;

    @Value("${open-api.fraud-number.base-url}")
    private String apiUrl;

    @Value("${open-api.fraud-number.secret-key}")
    private String apiKey;

    /**
     * 주어진 번호에 대해 스팸 여부를 조회합니다.
     * @param number 전화번호(국제전화, 번호 중간 "-" 허용, "-" 없이 숫자만 이어 붙인 번호 허용)
     * @return CheckSpamNumberResponse.DataBlock
     */
    public CheckFraudNumberResponse.DataBlock checkSpamNumber(String number){
        HttpHeaders headers = new HttpHeaders();
        headers.add("CL_AUTH_KEY", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("number", number);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        try{
            ResponseEntity<CheckFraudNumberResponse> response =
                    restTemplate.postForEntity(
                            apiUrl,
                            request,
                            CheckFraudNumberResponse.class
                    );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("FraudNumber API error: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new BusinessExceptionHandler(ErrorCode.FRAUD_NUMBER_SERVER_ERROR);
            }

            CheckFraudNumberResponse.DataBlock data = response.getBody().getData();
            if (data.getSuccess() != 1) {
                log.error("SpamNumber API returned success!=1: {}", data);
                throw new BusinessExceptionHandler(ErrorCode.FRAUD_NUMBER_SERVER_ERROR);
            }
            log.info("FraudNumber API Success, getSpamCount: {}", response.getBody().getData().getSpamCount());
            return data;

        } catch (RestClientException ex){
            log.error("Failed to call FraudNumber API", ex);
            throw new BusinessExceptionHandler(ErrorCode.FRAUD_NUMBER_SERVER_ERROR);
        }
    }

}
