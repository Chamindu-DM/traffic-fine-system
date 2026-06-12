package com.trafficfine.sms;

public record SmsSendResult(boolean successful, String providerResponse, String errorMessage) {

    public static SmsSendResult success(String providerResponse) {
        return new SmsSendResult(true, providerResponse, null);
    }

    public static SmsSendResult failure(String providerResponse, String errorMessage) {
        return new SmsSendResult(false, providerResponse, errorMessage);
    }
}