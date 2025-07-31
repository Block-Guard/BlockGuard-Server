package com.blockguard.server.domain.news.scheduler;

import com.blockguard.server.domain.news.dao.NewsRepository;
import com.blockguard.server.domain.news.domain.NewsArticle;
import com.blockguard.server.domain.news.domain.enums.Category;
import com.blockguard.server.domain.news.dto.response.NewsArticleResponse;
import com.blockguard.server.infra.naver.news.NaverNewsOpenApiClient;
import com.blockguard.server.infra.naver.news.crawler.ImageCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewsSaveScheduler {
    private final NaverNewsOpenApiClient naverNewsOpenApiClient;
    private final ImageCrawler imageCrawler;
    private final NewsRepository newsRepository;

    private final List<String> categories = List.of("보이스피싱", "스미싱", "메신저 피싱");

    private final Set<String> blockedDomains = Set.of("allurekorea.com", "fashionn.com", "wedding21news.co.kr");

    private boolean isBlocked(String url) {
        return blockedDomains.stream().anyMatch(url::contains);
    }

    // @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void saveNewsArticlesScheduler() {
        for (String category : categories) {
            List<NewsArticleResponse> fetched = naverNewsOpenApiClient.fetchNews(category);

            for (NewsArticleResponse newsArticleResponse : fetched) {
                boolean exists = newsRepository.existsByUrl(newsArticleResponse.getUrl());
                if (exists) continue;
                if (!newsArticleResponse.getTitle().contains(category)) {
                    log.info("❌ 제목에 키워드 포함 안 됨 → '{}': {}", category, newsArticleResponse.getTitle());
                    continue;
                }
                if (newsRepository.existsByUrl(newsArticleResponse.getUrl())) {
                    log.info("❌ 중복 URL로 저장 건너뜀: {}", newsArticleResponse.getUrl());
                    continue;
                }
                String imageUrl = imageCrawler.fetchOgImage(newsArticleResponse.getUrl());

                NewsArticle article = NewsArticle.builder()
                        .title(newsArticleResponse.getTitle())
                        .url(newsArticleResponse.getUrl())
                        .publishedAt(LocalDateTime.parse(newsArticleResponse.getPublishedAt()))
                        .newspaper(newsArticleResponse.getNewspaper())
                        .imageUrl(imageUrl)
                        .category(Category.from(category))
                        .build();

                newsRepository.save(article);
            }

        }
    }
}
