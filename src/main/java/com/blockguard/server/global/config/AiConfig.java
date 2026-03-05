package com.blockguard.server.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class AiConfig {

    private final String scoreTable = loadScoreTable();

    private String loadScoreTable() {
        try {
            return new ClassPathResource("prompt/score-table.txt")
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("score-table.txt 로딩 실패", e);
        }
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(buildSystemPrompt())
                .build();
    }

    private String buildSystemPrompt() {
        return "너는 보이스피싱 사기 탐지 전문가다.\n"
                + "다음 규칙표를 사용해 입력 텍스트를 분석하고,\n"
                + "1) 사기 유형(카테고리) 선택\n"
                + "2) 핵심 키워드(최소1~최대3) 추출\n"
                + "3) 해당 유형으로 분류한 이유를 한 두 문장으로 설명\n"
                + "4) 점수는 최소 0점에서 최대 70점\n"
                + "\n"
                + "[점수 규칙]\n"
                + "- 규칙표는 \"카테고리ID 카테고리명\" 한 줄 + 그 아래 여러 룰 라인으로 구성된다.\n"
                + "- 룰 라인 형식: \"룰ID:배점:키워드1|키워드2|...\".\n"
                + "- 'messageContent', 'keywords', 'additionalDescription'에 해당 룰의 키워드 중 1개라도 의미/표현상 해당 룰에 해당된다면 그 룰의 배점을 가산(룰별 최대 1회).\n"
                + "- 'imageContent'는 유형 분류에 참고할 것.\n"
                + "- 유저가 입력한 'keywords', 'additionalDescription'에 매칭되는 키워드가 있으면 점수 계산.\n"
                + "- 규칙표의 각자 룰들의 키워드들의 유사성을 분석하고, 유사한 키워드도 해당 룰에 매칭여부를 관대하게 판단할 것을 명심해라.\n"
                + "- 키워드는 의미가 같으면 띄어쓰기/대소문자/오타 등과 동의어 허용.\n"
                + "- 동일 카테고리에서 여러 룰이 적중할 수 있으며, 합산 후 카테고리 점수는 70을 초과하지 않는다.\n"
                + "- 최종적으로 가장 점수가 높은 카테고리를 estimatedFraudType으로 선택.\n"
                + "- 동점이면 적중 룰 개수가 더 많은 카테고리를 선택. 그래도 동률이면 의미상 더 근접한 쪽.\n"
                + "- 링크/단축URL은 http/https, bit.ly/han.gl/is.gd/vo.la/me2.do 등도 매칭으로 본다(표기 변형 허용).\n"
                + "\n"
                + "출력은 반드시 valid JSON 객체로만 응답. 오직 JSON만 출력.\n"
                + "코드블록(```), 주석, 추가 텍스트, 설명 모두 금지.\n"
                + "'estimatedFraudType' 에는 오로지 카테고리명만 넣을 것.\n"
                + "아래는 응답 예시.\n"
                + "{{\n"
                + "  \"estimatedFraudType\": \"카테고리명\",\n"
                + "  \"keywords\": [\"키워드1\", \"키워드2\", \"키워드3\"],\n"
                + "  \"explanation\": \"왜 이 유형이고 어떤 이유로 판단하였는지 간결히 설명\",\n"
                + "  \"score\": 0\n"
                + "}}\n"
                + "\n"
                + "[규칙표]\n"
                + scoreTable;
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}