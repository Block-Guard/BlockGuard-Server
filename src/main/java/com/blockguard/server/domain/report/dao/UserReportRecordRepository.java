package com.blockguard.server.domain.report.dao;

import com.blockguard.server.domain.report.domain.UserReportRecord;
import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRecordRepository extends JpaRepository<UserReportRecord, Long> {
    boolean existsByUserAndIsCompletedFalse(User user);
}
