package com.blockguard.server.infra.gpt;

import com.blockguard.server.domain.analysis.dto.request.GptRequest;
import com.blockguard.server.domain.analysis.dto.response.GptResponse;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptApiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public GptResponse analyze(GptRequest gptRequest) {
        log.info("gpt server 사기 분석 api 호출");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GptRequest> entity = new HttpEntity<>(gptRequest, headers);

        try {
            ResponseEntity<GptResponse> response = restTemplate.postForEntity(
                    aiServerUrl,
                    entity,
                    GptResponse.class
            );

            if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
                log.error("GPT API error: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new BusinessExceptionHandler(ErrorCode.AI_SERVER_ERROR);
            }
            return response.getBody();

        } catch (RestClientException e){
            log.error("GPT API communication failed: {}", e.getMessage());
            throw new BusinessExceptionHandler(ErrorCode.AI_SERVER_ERROR);
        }
    }
}
