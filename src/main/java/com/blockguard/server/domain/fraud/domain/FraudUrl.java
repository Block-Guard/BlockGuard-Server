package com.blockguard.server.domain.fraud.domain;

import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(nullable = false)
    private LocalDate detectedDate;

    @Column(name = "last_checked_at", nullable = false)
    private LocalDateTime lastCheckedAt;

}
