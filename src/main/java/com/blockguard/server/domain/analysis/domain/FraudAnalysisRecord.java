package com.blockguard.server.domain.analysis.domain;

import com.blockguard.server.domain.analysis.domain.enums.FraudType;
import com.blockguard.server.domain.analysis.domain.enums.RiskLevel;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "fraud_analysis_records")
public class FraudAnalysisRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "estimated_fraud_type")
    private FraudType estimatedFraudType;

    @Column(length = 255)
    private String keywords;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
