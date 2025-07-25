package com.blockguard.server.infra.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverOcrClient {

    @Value("${naver.ocr.invoke-url}")
    private String invokeUrl;

    @Value("${naver.ocr.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String extractTextFromImage(List<String> imageUrls) {
        List<Map<String, Object>> images = imageUrls.stream()
                .map(url -> {
                    Map<String, Object> image = Map.of(
                        "format", "jpg",
                        "name", "ocrImage",
                        "url", url
                    );
                    return image;
                })
                .collect(Collectors.toList());

        Map<String, Object> payload = Map.of(
                "version", "V2",
                "requestId", UUID.randomUUID().toString(),
                "timestamp", System.currentTimeMillis(),
                "lang", "ko",
                "images", images,
                "enableTableDetection", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-OCR-SECRET", secretKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    invokeUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            List<Map<String, Object>> imageResponses = (List<Map<String, Object>>) response.getBody().get("images");

            return imageResponses.stream()
                    .flatMap(image -> ((List<Map<String, Object>>) image.get("fields")).stream())
                    .map(field -> (String) field.get("inferText"))
                    .collect(Collectors.joining(" "));

        } catch (Exception e) {
            log.error("❌ OCR 실패: {}", e.getMessage());
            return "";
        }
    }
}
