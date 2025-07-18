package com.blockguard.server.domain.guardian.dao;

import com.blockguard.server.domain.guardian.domain.Guardian;
import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    List<Guardian> findAllByUserId(Long userId);
    Optional<Guardian> findByIdAndUser(Long id, User user);

    @Modifying
    @Query("""
      UPDATE Guardian g
      SET g.isPrimary = false
      WHERE g.user = :user
        AND g.deletedAt IS NULL
        AND g.isPrimary = true
  """)
    void clearPrimaryFlagsByUser(User user);

    boolean existsByUserAndNameAndDeletedAtIsNull(User user, String name);
}
