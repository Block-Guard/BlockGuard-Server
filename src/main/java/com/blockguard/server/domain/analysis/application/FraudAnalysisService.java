package com.blockguard.server.domain.analysis.application;

import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.request.GptRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.domain.analysis.dto.response.GptResponse;
import com.blockguard.server.domain.fraud.application.FraudService;
import com.blockguard.server.domain.fraud.dto.request.FraudPhoneNumberRequest;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.infra.gpt.GptApiClient;
import com.blockguard.server.infra.naver.ocr.NaverOcrClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final NaverOcrClient naverOcrClient;
    private final GptApiClient gptApiClient;
    private final FraudService fraudService;

    public FraudAnalysisResponse fraudAnalysis(FraudAnalysisRequest fraudAnalysisRequest, List<MultipartFile> imageFiles) {
        List<String> keywords = new ArrayList<>();
        double score = 0;

        extractKeywordsFromRequest(fraudAnalysisRequest, keywords);

        score = addFraudUrlScore(fraudAnalysisRequest, score);
        score = addFraudPhoneNumberScore(fraudAnalysisRequest, score);

        String additionalDescription = fraudAnalysisRequest.getAdditionalDescription();
        String imageContent = "";
        imageContent = extractOcrText(imageFiles);

        GptRequest gptRequest = buildGptRequest(fraudAnalysisRequest, keywords, additionalDescription, imageContent);

        // ai 서버 호출
        GptResponse gptResponse = gptApiClient.analyze(gptRequest);

        score += gptResponse.getScore();
        return FraudAnalysisResponse.builder()
                .keywords(gptResponse.getKeywords())
                .score(gptResponse.getScore())
                .estimatedFraudType(gptResponse.getEstimatedFraudType())
                .explanation(gptResponse.getExplanation())
                .riskLevel(RiskLevel.fromScore(score).getValue())
                .build();
    }

    private double addFraudPhoneNumberScore(FraudAnalysisRequest fraudAnalysisRequest, double score) {
        if (StringUtils.hasText(fraudAnalysisRequest.getSuspiciousPhoneNumbers())) {
            // 전화번호 위험이면 score 15점 추가
            String number = fraudAnalysisRequest.getSuspiciousPhoneNumbers().replaceAll("\\D+", "");
            if (number.isEmpty() &&
                    fraudService.checkFraudPhoneNumber(new FraudPhoneNumberRequest(number))
                            .getRiskLevel() == RiskLevel.Dangers){
                score += 15;
            }
        }
        return score;
    }

    private double addFraudUrlScore(FraudAnalysisRequest fraudAnalysisRequest, double score) {
        if (StringUtils.hasText(fraudAnalysisRequest.getSuspiciousLinks())) {
            // 링크 위험이면 score 15점 추가
            String link = fraudAnalysisRequest.getSuspiciousLinks().trim();
            if (!link.isEmpty() &&
                    fraudService.checkFraudUrl(new FraudUrlRequest(link))
                            .getRiskLevel() == RiskLevel.Dangers) {
                score += 15;
            }
        }
        return score;
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
        if (StringUtils.hasText(fraudAnalysisRequest.getAppType()))
            keywords.add(fraudAnalysisRequest.getAppType());
        if (fraudAnalysisRequest.getAtmGuided())
            keywords.add("긴급성이나 위기감을 느끼게 하는 표현이 있었다");
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
