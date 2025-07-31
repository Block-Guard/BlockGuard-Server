package com.blockguard.server.infra.naver.news.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageCrawlerTest {

    @Autowired
    private ImageCrawler imageCrawler;

    @Test
    void testFetchOgImage() {
        String testUrl = "https://n.news.naver.com/article/056/0012000253?cds=news_media_pc&type=editn";
        String imageUrl = imageCrawler.fetchOgImage(testUrl);
        System.out.println("추출된 이미지 URL: " + imageUrl);
    }

}