package com.blockguard.server.infra.naver.news.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImageCrawler {
    public String fetchOgImage(String articleUrl) {
        try {
            Document doc = Jsoup.connect(articleUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get();

            String ogImage = doc.select("meta[property=og:image]").attr("content");
            if (ogImage != null && !ogImage.isEmpty()) {
                return ogImage;
            }

            Element firstImg = doc.selectFirst("img");
            return firstImg != null ? firstImg.absUrl("src") : null;

        } catch (Exception e) {
            log.warn("이미지 크롤링 실패: {}", articleUrl, e);
            return null;
        }
    }
}
