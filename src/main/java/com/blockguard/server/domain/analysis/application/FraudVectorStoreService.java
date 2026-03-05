package com.blockguard.server.domain.analysis.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudVectorStoreService {

    private final VectorStore vectorStore;

    public List<Document> searchSimilarCases(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
        );
    }

    public String formatDocumentsForPrompt(List<Document> documents) {
        return documents.stream()
                .map(this::formatDocument)
                .collect(Collectors.joining("\n"));
    }

    private String formatDocument(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        return "유형: " + metadata.get("typeName") +
               " / 키워드: " + metadata.get("keywords") +
               " / 유사문자: " + document.getText() +
               " / 설명: " + metadata.get("description");
    }
}