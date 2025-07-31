package com.blockguard.server.domain.news.dao;

import com.blockguard.server.domain.news.domain.NewsArticle;
import com.blockguard.server.domain.news.domain.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsArticle, Long> {
    Page<NewsArticle> findByCategory(Category category, Pageable pageable);
    Page<NewsArticle> findAll(Pageable pageable);

    boolean existsByUrl(String url);
}
