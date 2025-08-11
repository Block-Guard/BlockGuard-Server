package com.blockguard.server.domain.analysis.application;

import com.blockguard.server.domain.analysis.dao.FraudAnalysisRecordRepository;
import com.blockguard.server.domain.analysis.domain.FraudAnalysisRecord;
import com.blockguard.server.domain.analysis.domain.enums.FraudType;
import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.analysis.dto.request.FraudAnalysisRequest;
import com.blockguard.server.domain.analysis.dto.request.GptRequest;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisRecordResponse;
import com.blockguard.server.domain.analysis.dto.response.FraudAnalysisResponse;
import com.blockguard.server.domain.analysis.dto.response.GptResponse;
import com.blockguard.server.domain.fraud.application.FraudService;
import com.blockguard.server.domain.fraud.dto.request.FraudPhoneNumberRequest;
import com.blockguard.server.domain.fraud.dto.request.FraudUrlRequest;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.infra.gpt.GptApiClient;
import com.blockguard.server.infra.naver.ocr.NaverOcrClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final NaverOcrClient naverOcrClient;
    private final GptApiClient gptApiClient;
    private final FraudService fraudService;
    private final FraudAnalysisRecordRepository fraudAnalysisRecordRepository;

    @Transactional
    public FraudAnalysisResponse fraudAnalysis(FraudAnalysisRequest fraudAnalysisRequest, List<MultipartFile> imageFiles, User user) {
        List<String> keywords = new ArrayList<>();
        double score = 0;

        score = addFraudUrlScore(fraudAnalysisRequest, score);
        score = addFraudPhoneNumberScore(fraudAnalysisRequest, score);

        extractKeywordsFromRequest(fraudAnalysisRequest, keywords);

        String additionalDescription = fraudAnalysisRequest.getAdditionalDescription();
        String imageContent = "";
        imageContent = extractOcrText(imageFiles);

        GptRequest gptRequest = buildGptRequest(fraudAnalysisRequest, keywords, additionalDescription, imageContent);

        // ai 서버 호출
        GptResponse gptResponse = gptApiClient.analyze(gptRequest);
        score = Math.min(100, score + gptResponse.getScore());

        // 사기 분석 기록 저장
        RiskLevel riskLevel = RiskLevel.fromScore(score);
        FraudType fraudTypeEnum = FraudType.fromKoreanName(gptResponse.getEstimatedFraudType());

        if (user != null) {
            fraudAnalysisRecordRepository.save(
                    FraudAnalysisRecord.builder()
                            .user(user)
                            .score(BigDecimal.valueOf(score))
                            .estimatedFraudType(riskLevel == RiskLevel.Safety ? null: fraudTypeEnum)
                            .riskLevel(riskLevel)
                            .build()
            );
        }

        return FraudAnalysisResponse.builder()
                .keywords(gptResponse.getKeywords())
                .score(score)
                .estimatedFraudType(gptResponse.getEstimatedFraudType())
                .explanation(gptResponse.getExplanation())
                .riskLevel(riskLevel.getValue())
                .build();
    }

    private double addFraudPhoneNumberScore(FraudAnalysisRequest fraudAnalysisRequest, double score) {
        if (StringUtils.hasText(fraudAnalysisRequest.getSuspiciousPhoneNumbers())) {
            // 전화번호 위험이면 score 10점 추가
            String number = fraudAnalysisRequest.getSuspiciousPhoneNumbers().replaceAll("\\D+", "");
            if (!number.isEmpty() &&
                    fraudService.checkFraudPhoneNumber(new FraudPhoneNumberRequest(number))
                            .getRiskLevel() == RiskLevel.Dangers){
                score += 10;
            }
        }
        return score;
    }

    private double addFraudUrlScore(FraudAnalysisRequest fraudAnalysisRequest, double score) {
        if (StringUtils.hasText(fraudAnalysisRequest.getSuspiciousLinks())) {
            // 링크 위험이면 score 10점 추가
            String link = fraudAnalysisRequest.getSuspiciousLinks().trim();
            if (!link.isEmpty() &&
                    fraudService.checkFraudUrl(new FraudUrlRequest(link))
                            .getRiskLevel() == RiskLevel.Dangers) {
                score += 10;
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

    private static void extractKeywordsFromRequest(FraudAnalysisRequest request, List<String> keywords) {
        if (StringUtils.hasText(request.getContactMethod()))
            keywords.add(request.getContactMethod());
        if (StringUtils.hasText(request.getCounterpart()))
            keywords.add(request.getCounterpart());
        if (request.getRequestedAction() != null && !request.getRequestedAction().isEmpty())
            keywords.addAll(request.getRequestedAction());
        if (request.getRequestedInfo() != null && !request.getRequestedInfo().isEmpty())
            keywords.addAll(request.getRequestedInfo());
        if (StringUtils.hasText(request.getLinkType()))
            keywords.add(request.getLinkType());
        if (Boolean.TRUE.equals(request.getPressuredInfo()))
            keywords.add("개인정보 유출/범죄 연루 언급 등 심리적 압박");
        if (Boolean.TRUE.equals(request.getAppOrLinkRequest()))
            keywords.add("앱 설치/링크 접속 유도");
        if (Boolean.TRUE.equals(request.getThirdPartyConnect()))
            keywords.add("제3자(수사관 등) 연결 시도");
        if (Boolean.TRUE.equals(request.getAuthorityPressure()))
            keywords.add("직책 강조 및 권위적 태도 보임");
        if (Boolean.TRUE.equals(request.getAccountOrLinkRequest()))
            keywords.add("계좌이체/현금인출 유도");
    }

    private static GptRequest buildGptRequest(FraudAnalysisRequest fraudAnalysisRequest, List<String> keywords, String additionalDescription, String imageContent) {
        return GptRequest.builder()
                .keywords(keywords)
                .additionalDescription(additionalDescription)
                .messageContent(StringUtils.hasText(fraudAnalysisRequest.getMessageContent()) ? fraudAnalysisRequest.getMessageContent() : null)
                .imageContent(StringUtils.hasText(imageContent) ? imageContent : null)
                .build();
    }

    public List<FraudAnalysisRecordResponse> getAnalyzeFraudList(User user){
        return fraudAnalysisRecordRepository.findAllByUserOrderByCreatedAtDesc(user).stream()
                .map(FraudAnalysisRecordResponse::from)
                .toList();
    }
}
