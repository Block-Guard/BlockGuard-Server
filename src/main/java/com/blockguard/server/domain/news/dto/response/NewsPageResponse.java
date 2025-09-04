package com.blockguard.server.domain.news.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsPageResponse {
    private List<NewsArticleResponse> news;
    private PageableInfo pageableInfo;
    private String sort;
}
