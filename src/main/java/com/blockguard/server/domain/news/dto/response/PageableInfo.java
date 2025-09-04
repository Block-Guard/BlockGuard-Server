package com.blockguard.server.domain.news.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableInfo {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
