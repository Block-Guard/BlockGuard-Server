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

    @Value("${fraud.number.api.invoke-url}")
    private String apiUrl;

    @Value("${fraud.number.api.secret-key}")
    private String apiKey;

    /**
     * 주어진 번호에 대해 스팸 여부를 조회합니다.
     * @param number "-" 없이 숫자만 이어 붙인 폰번호(ex: "01012341234")
     * @return CheckSpamNumberResponse.DataBlock
     */
    // Todo: 국제번호의 + 가능한지 체크
    public CheckFraudNumberResponse.DataBlock checkSpamNumber(String number){
        HttpHeaders headers = new HttpHeaders();
        headers.add("CL_AUTH_KEY", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("number", number);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

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
                log.warn("SpamNumber API returned success!=1: {}", data);
            }

            return data;

        } catch (RestClientException ex){
            log.error("Failed to call FraudNumber API", ex);
            throw new BusinessExceptionHandler(ErrorCode.FRAUD_NUMBER_SERVER_ERROR);
        }
    }

}
