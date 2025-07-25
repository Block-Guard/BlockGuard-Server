package com.blockguard.server.domain.analysis.application;

import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.request.GptRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.infra.ocr.NaverOcrClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FraudAnalysisService {

    private final NaverOcrClient naverOcrClient;

    public FraudAnalysisResponse fraudAnalysis(FraudAnalysisRequest fraudAnalysisRequest) {
        List<String> keywords = new ArrayList<>();

        String additionalDescription = extractKeywordsFromRequest(fraudAnalysisRequest, keywords);
        String imageContent = extractOcrText(fraudAnalysisRequest);
        sendToGpt(fraudAnalysisRequest, keywords, additionalDescription, imageContent);

        FraudAnalysisResponse fraudAnalysisResponse = FraudAnalysisResponse
                .builder()
                .keywords(keywords)
                .additionalDescription(additionalDescription)
                .messageContent(StringUtils.hasText(fraudAnalysisRequest.getMessageContent()) ? fraudAnalysisRequest.getMessageContent() : null)
                .imageContent(StringUtils.hasText(imageContent) ? imageContent : null)
                .build();

        return fraudAnalysisResponse;

    }

    private String extractOcrText(FraudAnalysisRequest fraudAnalysisRequest) {
        String imageContent = "";
        List<String> imageUrls = fraudAnalysisRequest.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()){
            List<String> imageContents = new ArrayList<>();
            imageUrls.forEach(url -> {
                String result = naverOcrClient.extractTextFromImage(List.of(url));
                if (!result.isBlank()) imageContents.add(result);
            });
            imageContent = String.join(" ", imageContents);
        }
        return imageContent;
    }

    private static String extractKeywordsFromRequest(FraudAnalysisRequest fraudAnalysisRequest, List<String> keywords) {
        if (StringUtils.hasText(fraudAnalysisRequest.getContactMethod()))
            keywords.add(fraudAnalysisRequest.getContactMethod());
        if (StringUtils.hasText(fraudAnalysisRequest.getCounterpart()))
            keywords.add(fraudAnalysisRequest.getCounterpart());
        if (fraudAnalysisRequest.getRequestedAction() != null && !fraudAnalysisRequest.getRequestedAction().isEmpty())
            keywords.addAll(fraudAnalysisRequest.getRequestedAction());
        if (fraudAnalysisRequest.getRequestedInfo() != null && !fraudAnalysisRequest.getRequestedInfo().isEmpty())
            keywords.addAll(fraudAnalysisRequest.getRequestedInfo());
        if (StringUtils.hasText(fraudAnalysisRequest.getAppType())) keywords.add(fraudAnalysisRequest.getAppType());
        String additionalDescription = fraudAnalysisRequest.getAdditionalDescription();
        return additionalDescription;
    }

    private static void sendToGpt(FraudAnalysisRequest fraudAnalysisRequest, List<String> keywords, String additionalDescription, String imageContent) {
        GptRequest gptRequest = GptRequest.builder()
                .keywords(keywords)
                .additionalDescription(additionalDescription)
                .messageContent(StringUtils.hasText(fraudAnalysisRequest.getMessageContent()) ? fraudAnalysisRequest.getMessageContent() : null)
                .imageContent(StringUtils.hasText(imageContent) ? imageContent : null)
                .build();
    }
}
