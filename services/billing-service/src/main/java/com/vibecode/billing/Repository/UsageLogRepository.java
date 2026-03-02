package com.vibecode.billing.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vibecode.billing.entity.UsageLog;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long>{
    boolean existsByEventId(String eventId);
}
