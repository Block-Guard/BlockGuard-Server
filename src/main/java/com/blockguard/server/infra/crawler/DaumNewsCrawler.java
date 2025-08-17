package com.blockguard.server.infra.crawler;

import com.blockguard.server.domain.news.dao.NewsRepository;
import com.blockguard.server.domain.news.domain.NewsArticle;
import com.blockguard.server.domain.news.domain.enums.Category;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DaumNewsCrawler {
    private final NewsRepository newsRepository;
    private static final int MAX_PAGES = 20;
    private static final int ARTICLE_RETENTION_DAYS = 1095;
    private static final long CRAWL_DELAY_MS = 500L;

    public void fetchNewsFromDaum(String keyword) {
        Category category = Category.from(keyword);
        int savedCount = 0;

        try {
            for (int page = 1; page <= MAX_PAGES; page++) {
                String searchUrl = "https://search.daum.net/search?w=news&q=" +
                        URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "&p=" + page;

                Document doc = Jsoup.connect(searchUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/114.0.0.0 Safari/537.36")
                        .referrer("https://www.google.com")
                        .get();

                Elements newsItems = doc.select("li[data-docid]");

                if (newsItems.isEmpty()) break;

                for (Element item : newsItems) {
                    Element titleEl = item.selectFirst("div.item-title strong.tit-g a");
                    if (titleEl == null) continue;

                    String title = titleEl.text();
                    String url = titleEl.attr("href");

                    if (newsRepository.existsByUrl(url)) continue;

                    Element imageEl = item.selectFirst("a.thumb_bf img");

                    String imageUrl = Optional.ofNullable(imageEl)
                            .map(img -> {
                                String[] candidates = {
                                        img.attr("data-original-src"),
                                        img.attr("data-src"),
                                        img.attr("data-original"),
                                        img.attr("src")
                                };
                                for (String candidate : candidates) {
                                    if (candidate != null) {
                                        String cleaned = candidate.trim();
                                        if (cleaned.startsWith("http") && !cleaned.startsWith("data:image")) {
                                            return cleaned;
                                        }
                                    }
                                }
                                return null;
                            })
                            .orElse(null);

                    String newspaper = Optional.ofNullable(item.selectFirst("a.item-writer span.txt_info"))
                            .map(Element::text)
                            .orElse("다음뉴스");

                    String timeText = item.select("div.item-contents span.gem-subinfo > span.txt_info").stream()
                            .map(Element::text)
                            .filter(t -> t.matches(".*(\\d{4}\\.|\\d+시간전|\\d+분전|\\d+시간 전|\\d+분 전|어제).*"))
                            .findFirst()
                            .orElse(null);

                    LocalDateTime publishedAt = parsePublishedAt(timeText);
                    if (publishedAt.isBefore(LocalDateTime.now().minusDays(ARTICLE_RETENTION_DAYS))) {
                        continue;
                    }

                    NewsArticle article = NewsArticle.builder()
                            .title(Jsoup.clean(title, Safelist.none()))
                            .url(url)
                            .publishedAt(publishedAt)
                            .newspaper(Jsoup.clean(newspaper, Safelist.none()))
                            .imageUrl(imageUrl)
                            .category(category)
                            .build();

                    newsRepository.save(article);
                    savedCount++;
                }
                Thread.sleep(CRAWL_DELAY_MS);
            }

        } catch (IOException e) {
            throw new BusinessExceptionHandler(ErrorCode.FAIL_TO_CRAWLING_NEWS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDateTime parsePublishedAt(String text) {
        if (text == null) return LocalDateTime.now();

        LocalDateTime now = LocalDateTime.now();

        if (text.matches("\\d{4}\\.\\d{2}\\.\\d{2}\\.?")) {
            return LocalDate.parse(text.replace(".", "").substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .atStartOfDay();
        } else if (text.contains("시간전") || text.contains("시간 전")) {
            int hours = Integer.parseInt(text.replaceAll("[^0-9]", ""));
            return now.minusHours(hours);
        } else if (text.contains("분전") || text.contains("분 전")) {
            int minutes = Integer.parseInt(text.replaceAll("[^0-9]", ""));
            return now.minusMinutes(minutes);
        } else if (text.contains("어제")) {
            return now.minusDays(1);
        }
        return now;
    }
}


