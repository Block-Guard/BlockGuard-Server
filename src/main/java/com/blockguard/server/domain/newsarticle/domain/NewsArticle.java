package com.blockguard.server.domain.newsarticle.domain;

import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(
        name = "news_articles",
        uniqueConstraints = @UniqueConstraint(name = "uk_news_article_url", columnNames = "url")
)
public class NewsArticle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @Column(nullable = false, length = 100)
    private String newspaper;
}
