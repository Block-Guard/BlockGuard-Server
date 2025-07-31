package com.blockguard.server.infra.naver.news.dto;

import com.blockguard.server.domain.news.dto.response.NewsArticleResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class NaverNewsItem {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;

    // 프론트 응답 형식으로 반환
    public NewsArticleResponse toDto() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME);

        return NewsArticleResponse.builder()
                .title(title.replaceAll("<.*?>", "")) // HTML 태그 제거
                .url(link)
                .newspaper(extractDomain(link))
                .publishedAt(zonedDateTime.toLocalDateTime().toString())
                .imageUrl(null)
                .build();
    }

    private String extractDomain(String link) {
        try {
            String host = new java.net.URL(link).getHost();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
