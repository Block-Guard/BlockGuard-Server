package com.blockguard.server.domain.news.dto.response;

import com.blockguard.server.domain.news.domain.NewsArticle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewsArticleResponse {
    private Long id;
    private String title;
    private String publishedAt;
    private String url;
    private String newspaper;
    private String imageUrl;

    public static NewsArticleResponse from(NewsArticle newsArticle) {
        return NewsArticleResponse.builder()
                .id(newsArticle.getId())
                .title(newsArticle.getTitle())
                .publishedAt(formatTime(newsArticle.getPublishedAt()))
                .imageUrl(newsArticle.getImageUrl())
                .newspaper(newsArticle.getNewspaper())
                .url(newsArticle.getUrl())
                .build();
    }


    private static String formatTime(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());

        if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else if (duration.toDays() == 1) {
            return "어제";
        } else {
            return time.toLocalDate().toString();
        }
    }
}
