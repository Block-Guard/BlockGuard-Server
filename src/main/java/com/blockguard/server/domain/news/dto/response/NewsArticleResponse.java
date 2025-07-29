package com.blockguard.server.domain.news.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsArticleResponse {
    private Long id;
    private String title;
    private String publishedAt;
    private String url;
    private String newspaper;
    private String imageUrl;
}
