package com.blockguard.server.infra.naver.news.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NaverNewsApiResponse {
    private String lastBuildDate;
    private int total; // 총 검색 결과 개수
    private int start; // 검색 시작 위치
    private int display; // 한 번에 표시할 검색 결과 개수
    private List<NaverNewsItem> items;

}
