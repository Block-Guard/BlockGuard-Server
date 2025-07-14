package com.blockguard.server.domain.fraud.domain;

import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "fraud_urls")
public class FraudUrl extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String url;

    // Todo: Open API 제공자 추후 enum type 변경
    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "last_checked_at", nullable = false)
    private LocalDateTime lastCheckedAt;

}
