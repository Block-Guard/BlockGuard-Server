package com.blockguard.server.domain.analysis.dao;

import com.blockguard.server.domain.analysis.domain.FraudAnalysisRecord;
import com.blockguard.server.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudAnalysisRecordRepository extends JpaRepository<FraudAnalysisRecord, Long> {
    List<FraudAnalysisRecord> findAllByUserOrderByCreatedAtDesc(User user);
}
