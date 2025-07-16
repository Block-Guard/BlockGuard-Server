package com.blockguard.server.domain.guardian.dao;

import com.blockguard.server.domain.guardian.domain.Guardian;
import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    List<Guardian> findAllByUserId(Long userId);
    Optional<Guardian> findByIdAndUser(Long id, User user);
}
