package com.blockguard.server.domain.news.api;

import com.blockguard.server.domain.news.application.NewsService;
import com.blockguard.server.domain.news.dto.response.NewsPageResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@AllArgsConstructor
@Slf4j
public class NewsApi {
    private final NewsService newsService;

    @GetMapping
    @CustomExceptionDescription(SwaggerResponseDescription.GET_NEWS_ARTICLES_FAIL)
    @Operation(summary = "뉴스 조회")
    public BaseResponse<NewsPageResponse> getNewsArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "published_at_desc") String sort,
            @RequestParam(defaultValue = "전체") String category) {
        NewsPageResponse newsPageResponse = newsService.getNewsList(page, size, sort, category);
        return BaseResponse.of(SuccessCode.GET_NEWS_ARTICLES_SUCCESS, newsPageResponse);
    }

}
