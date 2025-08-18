package com.blockguard.server.domain.news.scheduler;

import com.blockguard.server.domain.news.domain.enums.Category;
import com.blockguard.server.infra.crawler.DaumNewsCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsSaveScheduler {
    private final DaumNewsCrawler daumNewsCrawler;
    private static final Map<Category, List<String>> KEYWORDS_PER_CATEGORY = Map.ofEntries(
            Map.entry(Category.VOICE_PHISHING, List.of("보이스피싱", "보이스 피싱")),
            Map.entry(Category.SMISHING, List.of("스미싱")),
            Map.entry(Category.MESSAGE_VOICE_PHISHING, List.of("메신저 피싱", "메신저피싱")),

            Map.entry(Category.INSTITUTION_IMPERSONATION, List.of("기관 사칭", "검찰 사칭", "경찰 사칭", "수사기관 사칭")),
            Map.entry(Category.LOAN_FRAUD, List.of("대출 사기", "저금리 대출 사기", "대출 빙자 사기")),
            Map.entry(Category.CARD_IMPERSONATION, List.of("카드사 사칭", "카드사 피싱", "신용카드 사칭")),
            Map.entry(Category.FAMILY_IMPERSONATION, List.of("가족 사칭", "지인 사칭")),
            Map.entry(Category.EVENT_IMPERSONATION, List.of("경조사 사칭", "조의금 사기", "경조사 문자 사기")),
            Map.entry(Category.PUBLIC_IMPERSONATION, List.of("공공기관 사칭", "국세청 사칭", "경찰 출석 요구 사칭", "검찰 사칭", "과태료 사칭")),
            Map.entry(Category.PART_TIME_SCAM, List.of("알바 사기", "부업 사기", "구인 사기")),
            Map.entry(Category.GOVERNMENT_GRANT_SCAM, List.of("정부지원금 사기", "보조금 사기", "지원금 사칭")),
            Map.entry(Category.DELIVERY_SCAM, List.of("택배 사기", "택배 문자 사기", "배송 사기")),
            Map.entry(Category.INVESTMENT_SCAM, List.of("투자 사기", "코인 투자 사기", "주식 리딩방 사기")),
            Map.entry(Category.FALSE_PAYMENT_SCAM, List.of("허위 결제 사기", "결제 승인 사기", "결제 피싱")),
            Map.entry(Category.ETC, List.of("몸캠"))
    );


    public void crawlingForAdmin() {
        crawlAll();
    }

    // @Scheduled(cron = "0 0 4 * * *")
    public void saveNewsArticles() {
        crawlAll();
    }

    private void crawlAll() {
        log.info("뉴스 크롤링 스케줄링 시작");
        KEYWORDS_PER_CATEGORY.forEach((category, keywords) -> {
            for (String keyword : keywords) {
                try {
                    daumNewsCrawler.fetchNewsFromDaum(keyword, category);
                } catch (Exception e) {
                    log.warn("크롤 실패: category={}, keyword={}", category, keyword, e);
                }
            }
        });
        log.info("뉴스 크롤링 스케줄링 완료");
    }
}
