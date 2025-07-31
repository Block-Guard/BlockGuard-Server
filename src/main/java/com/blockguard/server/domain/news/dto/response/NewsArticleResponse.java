package com.blockguard.server.domain.news.dto.response;

import com.blockguard.server.domain.news.domain.NewsArticle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class NewsArticleResponse {
    private Long id;
    private String title;
    private String publishedAt;
    private String url;
    private String newspaper;
    private String imageUrl;

    public static NewsArticleResponse from(NewsArticle newsArticle) {
        return NewsArticleResponse.builder()
                .title(newsArticle.getTitle())
                .publishedAt(newsArticle.getPublishedAt().toString())
                .imageUrl(newsArticle.getImageUrl())
                .newspaper(newsArticle.getNewspaper())
                .url(newsArticle.getUrl())
                .build();
    }
}
