package com.trafficfine.sms;

public interface SmsProvider {

    SmsSendResult sendSms(String phoneNumber, String message);
}