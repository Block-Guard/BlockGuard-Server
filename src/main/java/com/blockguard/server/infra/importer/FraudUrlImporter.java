package com.blockguard.server.infra.importer;

import com.blockguard.server.domain.fraud.dao.FraudUrlRepository;
import com.blockguard.server.domain.fraud.domain.FraudUrl;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudUrlImporter {

    private static final int PER_PAGE = 50;
    private static final int MAX_PAGE = 1000;
    private static final int DELAY_BETWEEN_REQUESTS_MS = 200;

    @Value("${open-api.fraud-url.base-url}")
    private String apiUrl;

    // 2023 version
    @Value("${open-api.fraud-url.base-url-old}")
    private String apiUrlOld;

    @Value("${open-api.fraud-url.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate;
    private final FraudUrlRepository fraudUrlRepository;

    @Async
    @Transactional
    public void syncFraudUrlsFromOpenApi() {

        log.info("2024 버전 사기 URL 동기화 시작");
        importFrom(apiUrl);

        log.info("2023 버전 사기 URL 동기화 시작");
        importFrom(apiUrlOld);

    }

    private void importFrom(String baseUrl) {
        int page = 1;
        boolean hasNext = true;

        while (hasNext && page <= MAX_PAGE) {

            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            String fullUrl = String.format("%s?page=%d&perPage=%d&serviceKey=%s",
                    baseUrl, page, PER_PAGE, encodedKey);

            URI requestUrl = URI.create(fullUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(requestUrl, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                if (data == null || data.isEmpty()) {
                    hasNext = false;
                } else {
                    for (Map<String, Object> item : data) {
                        String detectedDateStr = (String) item.get("날짜");
                        String urlStr = (String) item.get("홈페이지주소");

                        if (urlStr == null || detectedDateStr == null) continue;

                        if (!fraudUrlRepository.existsByUrl(urlStr)) {
                            LocalDate detectedDate = LocalDate.parse(detectedDateStr);
                            fraudUrlRepository.save(FraudUrl.builder()
                                    .url(urlStr)
                                    .detectedDate(detectedDate)
                                    .lastCheckedAt(LocalDateTime.now())
                                    .build());
                        }
                    }
                    page++;
                }

                try {
                    Thread.sleep(DELAY_BETWEEN_REQUESTS_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BusinessExceptionHandler(ErrorCode.FAIL_IMPORT_OPEN_API);
                }

            } else {
                log.error("API 요청 실패 - {}", response.getStatusCode());
                throw new BusinessExceptionHandler(ErrorCode.FAIL_IMPORT_OPEN_API);
            }
        }
    }
}
