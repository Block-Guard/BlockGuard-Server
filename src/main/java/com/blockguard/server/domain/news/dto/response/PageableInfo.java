package com.blockguard.server.domain.news.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PageableInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
