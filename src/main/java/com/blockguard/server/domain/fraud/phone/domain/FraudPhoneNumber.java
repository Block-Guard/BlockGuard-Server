package com.blockguard.server.domain.fraud.phone.domain;

import com.blockguard.server.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "fraud_phone_numbers")
public class FraudPhoneNumber extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "phone_number", nullable = false, length = 20, unique = true)
    private String phoneNumber;

    // Todo: Open API 제공자 추후 enum type 변경
    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "last_checked_at", nullable = false)
    private LocalDateTime lastCheckedAt;

}
