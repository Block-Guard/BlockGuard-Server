package com.blockguard.server.infra.naver.news;

import com.blockguard.server.domain.news.dto.response.NewsArticleResponse;
import com.blockguard.server.infra.naver.news.dto.NaverNewsApiResponse;
import com.blockguard.server.infra.naver.news.dto.NaverNewsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverNewsOpenApiClient {
    @Value("${naver.news.client-id}")
    private String clientId;

    @Value("${naver.news.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public List<NewsArticleResponse> fetchNews(String keyword) {
        // String url = "https://openapi.naver.com/v1/search/news.json?query=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "&display=100&sort=sim";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<NewsArticleResponse> allArticles = new ArrayList<>();

        for (int start = 1; start <= 1000; start += 100) {
            String url = "https://openapi.naver.com/v1/search/news.json?query=" +
                    URLEncoder.encode(keyword, StandardCharsets.UTF_8) +
                    "&display=100&start=" + start +
                    "&sort=date"; // or "sim"

            ResponseEntity<NaverNewsApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, NaverNewsApiResponse.class);

            List<NewsArticleResponse> items = response.getBody().getItems().stream()
                    .map(NaverNewsItem::toDto)
                    .toList();

            allArticles.addAll(items);

            // 종료 조건: 마지막 페이지면 break
            if (items.size() < 100) break;

            // Optional: 속도 제한을 위해 잠깐 sleep
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }

        return allArticles;
    }
}
