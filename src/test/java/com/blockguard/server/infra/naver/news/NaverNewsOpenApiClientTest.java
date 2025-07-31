package com.blockguard.server.infra.naver.news;

import com.blockguard.server.domain.news.dto.response.NewsArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class NaverNewsOpenApiClientTest {

    @Autowired
    private NaverNewsOpenApiClient naverNewsOpenApiClient;

    @Test
    void fetchNewsShouldReturnArticles(){
        String keyword = "보이스피싱";

        List<NewsArticleResponse> responseList = naverNewsOpenApiClient.fetchNews(keyword);
        responseList.forEach(article -> {
            System.out.println("📌 제목: " + article.getTitle());
            System.out.println("📰 신문사: " + article.getNewspaper());
            System.out.println("🕒 발행일: " + article.getPublishedAt());
            System.out.println("🔗 링크: " + article.getUrl());
            System.out.println("🖼️ 이미지: " + article.getImageUrl());
            System.out.println("--------------------------------------------------");
        });
    }

}