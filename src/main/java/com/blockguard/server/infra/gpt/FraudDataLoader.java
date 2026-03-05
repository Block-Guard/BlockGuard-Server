package com.blockguard.server.infra.gpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudDataLoader implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/fraud-data.json");
        FraudDataWrapper wrapper = objectMapper.readValue(resource.getInputStream(), FraudDataWrapper.class);

        List<Document> documents = new ArrayList<>();

        for (FraudExample example : wrapper.fraudExamples()) {
            String typeName = example.typeName();
            String keywords = String.join(", ", example.keywords());

            for (FraudCase fraudCase : example.cases()) {
                Document document = new Document(
                        fraudCase.messageContent(),
                        Map.of(
                                "typeName", typeName,
                                "keywords", keywords,
                                "description", fraudCase.description()
                        )
                );
                documents.add(document);
            }
        }

        vectorStore.add(documents);
        log.info("FraudDataLoader: {}개의 사기 사례 벡터 저장 완료", documents.size());
    }

    record FraudDataWrapper(
            @JsonProperty("fraud_examples") List<FraudExample> fraudExamples
    ) {}

    record FraudExample(
            @JsonProperty("type_name") String typeName,
            @JsonProperty("keywords") List<String> keywords,
            @JsonProperty("cases") List<FraudCase> cases
    ) {}

    record FraudCase(
            @JsonProperty("message_content") String messageContent,
            @JsonProperty("description") String description
    ) {}
}