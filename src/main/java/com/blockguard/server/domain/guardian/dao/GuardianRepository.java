package com.blockguard.server.domain.guardian.dao;

import com.blockguard.server.domain.guardian.domain.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    List<Guardian> findAllByUserId(Long userId);
}
