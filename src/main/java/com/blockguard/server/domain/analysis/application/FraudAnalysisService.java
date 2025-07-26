package com.blockguard.server.domain.analysis.application;

import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.request.GptRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.domain.analysis.dto.response.GptResponse;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import com.blockguard.server.infra.ocr.NaverOcrClient;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final NaverOcrClient naverOcrClient;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiBaseUrl;

    public FraudAnalysisResponse fraudAnalysis(FraudAnalysisRequest fraudAnalysisRequest, List<MultipartFile> imageFiles) {
        List<String> keywords = new ArrayList<>();

        extractKeywordsFromRequest(fraudAnalysisRequest, keywords);
        String additionalDescription = fraudAnalysisRequest.getAdditionalDescription();
        String imageContent = "";
        imageContent = extractOcrText(imageFiles);

        GptRequest gptRequest = buildGptRequest(fraudAnalysisRequest, keywords, additionalDescription, imageContent);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GptRequest> entity = new HttpEntity<>(gptRequest, headers);
            ResponseEntity<GptResponse> response = restTemplate.postForEntity(
                    aiBaseUrl,
                    entity,
                    GptResponse.class
            );

            if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
                log.error("AI 서버 응답 오류: status={}, body={}", response.getStatusCode(), response.getBody());
                throw new BusinessExceptionHandler(ErrorCode.AI_SERVER_ERROR);
            }

            double score = response.getBody().getScore();
            return FraudAnalysisResponse
                    .builder()
                    .keywords(response.getBody().getKeywords())
                    .score(response.getBody().getScore())
                    .estimatedFraudType(response.getBody().getEstimatedFraudType())
                    .explanation(response.getBody().getExplanation())
                    .riskLevel(RiskLevel.fromScore(score).getName())
                    .build();

        } catch (RestClientException e){
            log.error("AI 서버 통신 실패: {}", e.getMessage());
            throw new BusinessExceptionHandler(ErrorCode.AI_SERVER_ERROR);
        }
    }

    private String extractOcrText(List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) return "";

        List<String> imageContents = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            try {
                byte[] imageBytes = file.getBytes();
                String fileName = file.getOriginalFilename();
                String result = naverOcrClient.extractTextFromImage(imageBytes, fileName);
                if (StringUtils.hasText(result)) {
                    imageContents.add(result);
                }
            } catch (IOException e) {
                log.error("이미지 OCR 실패: {}", e.getMessage());
            }
        }

        return String.join(" ", imageContents);
    }

    private static void extractKeywordsFromRequest(FraudAnalysisRequest fraudAnalysisRequest, List<String> keywords) {
        if (StringUtils.hasText(fraudAnalysisRequest.getContactMethod()))
            keywords.add(fraudAnalysisRequest.getContactMethod());
        if (StringUtils.hasText(fraudAnalysisRequest.getCounterpart()))
            keywords.add(fraudAnalysisRequest.getCounterpart());
        if (fraudAnalysisRequest.getRequestedAction() != null && !fraudAnalysisRequest.getRequestedAction().isEmpty())
            keywords.addAll(fraudAnalysisRequest.getRequestedAction());
        if (fraudAnalysisRequest.getRequestedInfo() != null && !fraudAnalysisRequest.getRequestedInfo().isEmpty())
            keywords.addAll(fraudAnalysisRequest.getRequestedInfo());
        if (StringUtils.hasText(fraudAnalysisRequest.getAppType())) keywords.add(fraudAnalysisRequest.getAppType());
    }

    private static GptRequest buildGptRequest(FraudAnalysisRequest fraudAnalysisRequest, List<String> keywords, String additionalDescription, String imageContent) {
        return GptRequest.builder()
                .keywords(keywords)
                .additionalDescription(additionalDescription)
                .messageContent(StringUtils.hasText(fraudAnalysisRequest.getMessageContent()) ? fraudAnalysisRequest.getMessageContent() : null)
                .imageContent(StringUtils.hasText(imageContent) ? imageContent : null)
                .build();
    }
}
