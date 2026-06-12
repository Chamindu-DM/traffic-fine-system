package com.trafficfine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sms")
public record SmsProperties(String provider, NotifyLk notifylk) {

    public record NotifyLk(String userId, String apiKey, String senderId) {
    }
}