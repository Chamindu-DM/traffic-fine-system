package com.trafficfine.notificationservice.sms;

public interface SmsProvider {
    SmsSendResult sendSms(String phoneNumber, String message);
}
