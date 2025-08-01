package com.blockguard.server.domain.news.scheduler;

import com.blockguard.server.infra.crawler.DaumNewsCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsSaveScheduler {
    private final DaumNewsCrawler daumNewsCrawler;

    public void crawlingForAdmin(){
        String[] keywords = {"보이스피싱", "스미싱", "메신저 피싱", "몸캠"};
        for (String keyword : keywords) {
            try {
                daumNewsCrawler.fetchNewsFromDaum(keyword);
            } catch (Exception e) {
                log.warn("Failed to crawl news for keyword: {}", keyword, e);
            }
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void saveNewsArticles(){
        log.info("뉴스 크롤링 스케줄링 시작");

        daumNewsCrawler.fetchNewsFromDaum("보이스피싱");
        daumNewsCrawler.fetchNewsFromDaum("스미싱");
        daumNewsCrawler.fetchNewsFromDaum("메신저 피싱");
        daumNewsCrawler.fetchNewsFromDaum("몸캠");

        log.info("뉴스 크롤링 스케줄링 완료");
    }
}
