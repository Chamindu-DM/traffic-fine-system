package com.trafficfine.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockSmsProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(MockSmsProvider.class);

    @Override
    public SmsSendResult sendSms(String phoneNumber, String message) {
        log.info("Mock SMS to {}: {}", phoneNumber, message);
        return SmsSendResult.success("Mock SMS recorded for " + phoneNumber);
    }
}