package com.trafficfine.integration;

import com.trafficfine.entity.Officer;
import com.trafficfine.entity.SmsLog;
import com.trafficfine.entity.SmsStatus;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.SmsLogRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockSmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsService.class);

    private final SmsLogRepository smsLogRepository;

    public MockSmsService(SmsLogRepository smsLogRepository) {
        this.smsLogRepository = smsLogRepository;
    }

    public SmsLog sendPaymentConfirmation(TrafficFine fine) {
        Officer officer = fine.getOfficer();
        String message = "Payment confirmed for fine " + fine.getReferenceNumber() + ". Driver license can be released.";
        log.info("Mock SMS to {}: {}", officer.getPhoneNumber(), message);
        return smsLogRepository.save(new SmsLog(fine, officer, officer.getPhoneNumber(), message, SmsStatus.SENT, LocalDateTime.now()));
    }
}
