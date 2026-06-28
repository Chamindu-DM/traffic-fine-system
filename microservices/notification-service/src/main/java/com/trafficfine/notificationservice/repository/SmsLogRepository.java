package com.trafficfine.notificationservice.repository;

import com.trafficfine.notificationservice.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
}
