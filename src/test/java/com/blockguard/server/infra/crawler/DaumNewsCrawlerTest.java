package com.blockguard.server.infra.crawler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DaumNewsCrawlerTest {
    @Autowired
    DaumNewsCrawler crawler;

    @Test
    void 크롤링_테스트() {
        crawler.fetchNewsFromDaum("보이스피싱");
        crawler.fetchNewsFromDaum("스미싱");
        crawler.fetchNewsFromDaum("메신저 피싱");
        crawler.fetchNewsFromDaum("몸캠");
    }
}