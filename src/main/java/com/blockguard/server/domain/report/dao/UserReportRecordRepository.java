package com.blockguard.server.domain.report.dao;

import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReportRecordRepository extends JpaRepository<UserReportRecord, Long> {
    boolean existsByUserAndIsCompletedFalse(User user);

    Optional<UserReportRecord> findFirstByUserAndIsCompletedFalseOrderByCreatedAtDesc(User user);

    Optional<UserReportRecord> findByIdAndUser(Long id, User user);
}
