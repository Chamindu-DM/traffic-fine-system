package com.trafficfine.service;

import com.trafficfine.config.SmsProperties;
import com.trafficfine.entity.Officer;
import com.trafficfine.entity.SmsLog;
import com.trafficfine.entity.SmsStatus;
import com.trafficfine.entity.TrafficFine;
import com.trafficfine.repository.SmsLogRepository;
import com.trafficfine.sms.SmsProvider;
import com.trafficfine.sms.SmsSendResult;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final SmsProvider smsProvider;
    private final SmsProvider mockSmsProvider;
    private final SmsProperties smsProperties;
    private final SmsLogRepository smsLogRepository;

    public SmsService(
            @Qualifier("smsProvider") SmsProvider smsProvider,
            @Qualifier("mockSmsProvider") SmsProvider mockSmsProvider,
            SmsProperties smsProperties,
            SmsLogRepository smsLogRepository) {
        this.smsProvider = smsProvider;
        this.mockSmsProvider = mockSmsProvider;
        this.smsProperties = smsProperties;
        this.smsLogRepository = smsLogRepository;
    }

    public SmsLog sendPaymentConfirmation(TrafficFine fine) {
        Officer officer = fine.getOfficer();
        String phoneNumber = officer.getPhoneNumber();
        String message = "Payment confirmed for fine " + fine.getReferenceNumber()
                + ". Driver license can be released.";

        SmsSendResult result = sendWithFallback(phoneNumber, message);
        SmsStatus status = result.successful() ? SmsStatus.SUCCESS : SmsStatus.FAILED;

        return smsLogRepository.save(new SmsLog(fine, officer, phoneNumber, message, status, LocalDateTime.now()));
    }

    private SmsSendResult sendWithFallback(String phoneNumber, String message) {
        try {
            SmsSendResult result = smsProvider.sendSms(phoneNumber, message);
            if (result.successful()) {
                log.info("SMS sent via {} for {}", smsProperties.provider(), phoneNumber);
                return result;
            }

            log.warn("SMS provider {} reported failure for {}: {}", smsProperties.provider(), phoneNumber,
                    result.errorMessage());
            attemptMockFallback(phoneNumber, message);
            return result;
        } catch (RuntimeException ex) {
            log.warn("SMS provider {} threw an exception for {}", smsProperties.provider(), phoneNumber, ex);
            attemptMockFallback(phoneNumber, message);
            return SmsSendResult.failure(null, ex.getMessage());
        }
    }

    private void attemptMockFallback(String phoneNumber, String message) {
        if (!"notifylk".equalsIgnoreCase(smsProperties.provider())) {
            return;
        }

        try {
            mockSmsProvider.sendSms(phoneNumber, message);
        } catch (RuntimeException fallbackEx) {
            log.warn("Mock SMS fallback failed for {}", phoneNumber, fallbackEx);
        }
    }
}