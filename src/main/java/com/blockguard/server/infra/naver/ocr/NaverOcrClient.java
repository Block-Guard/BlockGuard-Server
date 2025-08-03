package com.blockguard.server.infra.naver.ocr;

import com.blockguard.server.infra.naver.ocr.ByteArrayResourceWithFilename;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    private final RestTemplate restTemplate;

    public String extractTextFromImage(byte[] imageBytes, String fileName) {
        log.info("ocr api 호출");

        try {
            String ext = getExtension(fileName);
            MediaType mediaType = getMediaType(ext);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", secretKey);

            Map<String, Object> message = Map.of(
                    "version", "V2",
                    "requestId", UUID.randomUUID().toString(),
                    "timestamp", System.currentTimeMillis(),
                    "lang", "ko",
                    "images", List.of(Map.of(
                            "format", ext,
                            "name", "image"
                    ))
            );

            HttpHeaders jsonHeader = new HttpHeaders();
            jsonHeader.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> messagePart = new HttpEntity<>(
                    new ObjectMapper().writeValueAsString(message), jsonHeader
            );

            HttpHeaders fileHeader = new HttpHeaders();
            fileHeader.setContentType(mediaType);
            HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(
                    new ByteArrayResourceWithFilename(imageBytes, fileName), fileHeader
            );

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", messagePart);
            body.add("file", filePart);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

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
            log.error("OCR 요청 실패", e);
            return "";
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(); // "jpg" or "png"
    }

    private MediaType getMediaType(String ext) {
        return switch (ext) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            default -> throw new IllegalArgumentException("Unsupported image format: " + ext);
        };
    }
}