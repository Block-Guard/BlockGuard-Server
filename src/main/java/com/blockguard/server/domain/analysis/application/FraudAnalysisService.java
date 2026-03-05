package com.blockguard.server.domain.analysis.application;

import com.blockguard.server.domain.analysis.dao.FraudAnalysisRecordRepository;
import com.blockguard.server.domain.analysis.domain.FraudAnalysisRecord;
import com.blockguard.server.domain.analysis.domain.enums.FraudType;
import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.analysis.dto.ai.FraudAnalysisResult;
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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
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
    private final FraudVectorStoreService fraudVectorStoreService;
    private final ChatClient chatClient;

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


        // 최종 점수 계산
        score = Math.min(100, score + getSurveyScore(fraudAnalysisRequest) + gptResponse.getScore());

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
                .estimatedFraudType(fraudTypeEnum.getKorName())
                .explanation(gptResponse.getExplanation())
                .riskLevel(riskLevel.getValue())
                .build();
    }

    @Transactional
    public FraudAnalysisResponse fraudAnalysisRAG(FraudAnalysisRequest fraudAnalysisRequest, List<MultipartFile> imageFiles, User user) {
        // 1. URL/전화번호/설문 가산점
        double score = 0;
        score = addFraudUrlScore(fraudAnalysisRequest, score);
        score = addFraudPhoneNumberScore(fraudAnalysisRequest, score);
        score += getSurveyScore(fraudAnalysisRequest);

        // 2. 키워드 추출
        List<String> keywords = new ArrayList<>();
        extractKeywordsFromRequest(fraudAnalysisRequest, keywords);

        // 3. OCR 텍스트 추출
        String imageContent = extractOcrText(imageFiles);

        // 4. 유사 사례 벡터 검색
        String query = StringUtils.hasText(fraudAnalysisRequest.getMessageContent())
                ? fraudAnalysisRequest.getMessageContent()
                : fraudAnalysisRequest.getAdditionalDescription();
        List<Document> similarCases = fraudVectorStoreService.searchSimilarCases(query);
        similarCases.forEach(doc -> log.info("[RAG 유사 사례] 유형={} / 유사문자={}",
                doc.getMetadata().get("typeName"), doc.getText()));
        String formattedCases = fraudVectorStoreService.formatDocumentsForPrompt(similarCases);

        // 5. few-shot 프롬프트 구성 및 ChatClient 호출
        String userPrompt = buildRagPrompt(fraudAnalysisRequest, keywords, imageContent, formattedCases);

        FraudAnalysisResult result = chatClient.prompt()
                .user(userPrompt)
                .call()
                .entity(FraudAnalysisResult.class);

        // 6. 최종 점수 계산 및 레벨 판정
        score = Math.min(100, score + result.getRiskScore());
        RiskLevel riskLevel = RiskLevel.fromScore(score);
        FraudType fraudTypeEnum = FraudType.fromKoreanName(result.getFraudType());

        if (user != null) {
            fraudAnalysisRecordRepository.save(
                    FraudAnalysisRecord.builder()
                            .user(user)
                            .score(BigDecimal.valueOf(score))
                            .estimatedFraudType(riskLevel == RiskLevel.Safety ? null : fraudTypeEnum)
                            .riskLevel(riskLevel)
                            .build()
            );
        }

        return FraudAnalysisResponse.builder()
                .keywords(keywords)
                .score(score)
                .estimatedFraudType(fraudTypeEnum.getKorName())
                .explanation(result.getReason())
                .riskLevel(riskLevel.getValue())
                .build();
    }

    private static String buildRagPrompt(FraudAnalysisRequest req, List<String> keywords, String imageContent, String formattedCases) {
        StringBuilder sb = new StringBuilder();
        sb.append("[유사 사례]\n").append(formattedCases).append("\n\n");
        sb.append("[분석 대상]\n");
        if (StringUtils.hasText(req.getMessageContent())) {
            sb.append("문자 내용: ").append(req.getMessageContent()).append("\n");
        }
        if (StringUtils.hasText(imageContent)) {
            sb.append("이미지 OCR: ").append(imageContent).append("\n");
        }
        if (!keywords.isEmpty()) {
            sb.append("키워드: ").append(String.join(", ", keywords)).append("\n");
        }
        sb.append("추가 설명: ").append(req.getAdditionalDescription()).append("\n\n");
        sb.append("아래 JSON 형식으로 분석 결과를 응답하라:\n")
          .append("{{\n")
          .append("  \"fraudType\": \"사기 유형명\",\n")
          .append("  \"riskScore\": 0~70의 정수,\n")
          .append("  \"reason\": \"판단 이유\",\n")
          .append("  \"recommendation\": \"대응 권고\"\n")
          .append("}}\n");
        return sb.toString();
    }

    private static int getSurveyScore(FraudAnalysisRequest fraudAnalysisRequest) {
        int addionalScore = 0;
        if (Boolean.TRUE.equals(fraudAnalysisRequest.getPressuredInfo()) || Boolean.TRUE.equals(fraudAnalysisRequest.getAuthorityPressure()))
            addionalScore += 5;
        if (Boolean.TRUE.equals(fraudAnalysisRequest.getThirdPartyConnect()) || Boolean.TRUE.equals(fraudAnalysisRequest.getAccountOrLinkRequest()))
            addionalScore += 5;
        return addionalScore;
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
        addIfKeyword(request.getContactMethod(), keywords);
        addIfKeyword(request.getCounterpart(), keywords);
        addAllIfKeyword(request.getRequestedAction(), keywords);
        addAllIfKeyword(request.getRequestedInfo(), keywords);
        addIfKeyword(request.getLinkType(), keywords);
    }

    private static void addIfKeyword(String v, List<String> keywords) {
        if (StringUtils.hasText(v) && isNotEtc(v))
            keywords.add(v);
    }

    private static void addAllIfKeyword(List<String> vlist, List<String> keywords) {
        if (vlist == null || vlist.isEmpty()) return;
        vlist.stream()
                .filter(StringUtils::hasText)
                .filter(FraudAnalysisService::isNotEtc)
                .forEach(keywords::add);
    }

    private static boolean isNotEtc(String v) {
        String t = v.trim().replaceAll("\\s+", "");
        if (t.isEmpty()) return false;
        return !t.equals("기타");
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
