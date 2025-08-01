package com.blockguard.server.domain.admin.api;

import com.blockguard.server.domain.news.application.NewsService;
import com.blockguard.server.domain.news.scheduler.NewsSaveScheduler;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.infra.crawler.DaumNewsCrawler;
import com.blockguard.server.infra.importer.FraudUrlImporter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class AdminApi {

    private final FraudUrlImporter fraudUrlImporter;
    private NewsSaveScheduler newsSaveScheduler;

    @PostMapping("/update/fraud-url")
    @Operation(summary = "공공 api 데이터 호출 - 관리자용")
    public BaseResponse<Void> syncFraudUrls() {
        fraudUrlImporter.syncFraudUrlsFromOpenApi();
        return BaseResponse.of(SuccessCode.IMPORT_OPEN_API_SUCCESS);
    }


    @PostMapping("/crawl")
    @Operation(summary = "뉴스 크롤링 - 관리자용")
    public BaseResponse<Void> crawlNewsManually() {
        newsSaveScheduler.crawlingForAdmin();
        return BaseResponse.of(SuccessCode.CRWAL_DAUM_NEWS_SUCCESS);
    }
}


