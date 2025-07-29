package com.blockguard.server.infra.google;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleSafeBrowsingService {
    private final RestTemplate restTemplate;

    @Value("${google.safe.api-key}")
    private String apiKey;

    public boolean isUrlSafe(String url){
        String googleUrl = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "client", Map.of(
                        "clientId", "blockguard-app",
                        "clientVersion", "1.0"
                ),
                "threatInfo", Map.of(
                        "threatTypes", List.of("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE"),
                        "platformTypes", List.of("ANY_PLATFORM"),
                        "threatEntryTypes", List.of("URL"),
                        "threatEntries", List.of(Map.of("url", url))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    googleUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map body = response.getBody();
            boolean isSafe = (body == null || body.get("matches") == null);
            log.info("google safe browsing api 호출");
            log.info("URL 검사 - URL: {}, isSafe: {}", url, isSafe);
            return isSafe;

        } catch (Exception e) {
            log.error("Google Safe Browsing API 요청 실패: {}", e.getMessage());
            return false;
        }

    }
}
