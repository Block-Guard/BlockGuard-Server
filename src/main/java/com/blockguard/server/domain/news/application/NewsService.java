package com.blockguard.server.domain.news.application;

import com.blockguard.server.domain.news.dao.NewsRepository;
import com.blockguard.server.domain.news.domain.NewsArticle;
import com.blockguard.server.domain.news.domain.enums.Category;
import com.blockguard.server.domain.news.dto.response.NewsArticleResponse;
import com.blockguard.server.domain.news.dto.response.NewsPageResponse;
import com.blockguard.server.domain.news.dto.response.PageableInfo;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsPageResponse getNewsList(int page, int size, String sort, String category) {
        Pageable pageable = PageRequest.of(page - 1, size, getSort(sort));
        Page<NewsArticle> newsPage;

        if (page < 1 || size < 1) {
            throw new BusinessExceptionHandler(ErrorCode.MUST_BE_POSITIVE_NUMBER);
        }

        if (category.equals("전체")) {
            newsPage = newsRepository.findAllByIsFilteredOutFalse(pageable);
        } else {
            Category enumCategory = Category.from(category);
            newsPage = newsRepository.findByCategoryAndIsFilteredOutFalse(enumCategory, pageable);
        }

        List<NewsArticleResponse> articleResponses = newsPage.stream()
                .map(NewsArticleResponse::from)
                .toList();

        return NewsPageResponse.builder()
                .news(articleResponses)
                .sort(sort)
                .pageableInfo(PageableInfo.builder()
                        .page(newsPage.getNumber() + 1)
                        .size(newsPage.getSize())
                        .totalElements(newsPage.getTotalElements())
                        .totalPages(newsPage.getTotalPages())
                        .build())
                .build();
    }

    private Sort getSort(String sort) {
        return switch (sort) {
            case "published_at_asc" -> Sort.by("publishedAt").ascending();
            case "published_at_desc" -> Sort.by("publishedAt").descending();
            default -> Sort.by("publishedAt").descending();
        };
    }


    public List<NewsArticleResponse> getSelectedArticles() {
        List<Long> selectedIds = List.of(1L, 2L, 3L, 4L, 5L, 6L);
        List<NewsArticle> articles = newsRepository.findAllById(selectedIds);

        return articles.stream().map(NewsArticleResponse::from)
                .toList();
    }
}
