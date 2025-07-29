package com.blockguard.server.domain.fraud.dao;

import com.blockguard.server.domain.fraud.domain.FraudUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudUrlRepository extends JpaRepository<FraudUrl, Long> {
    boolean existsByUrl(String url);
    Optional<FraudUrl> findByUrl(String url);
}
