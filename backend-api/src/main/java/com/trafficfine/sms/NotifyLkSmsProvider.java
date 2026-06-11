package com.trafficfine.sms;

import com.trafficfine.config.SmsProperties;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class NotifyLkSmsProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(NotifyLkSmsProvider.class);
    private static final String ENDPOINT = "https://app.notify.lk/api/v1/send";

    private final RestTemplate restTemplate;
    private final SmsProperties smsProperties;

    public NotifyLkSmsProvider(RestTemplate restTemplate, SmsProperties smsProperties) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate must not be null");
        this.smsProperties = Objects.requireNonNull(smsProperties, "smsProperties must not be null");
    }

    @Override
    public SmsSendResult sendSms(String phoneNumber, String message) {
        SmsProperties.NotifyLk properties = smsProperties.notifylk();
        if (properties == null
                || !StringUtils.hasText(properties.userId())
                || !StringUtils.hasText(properties.apiKey())
                || !StringUtils.hasText(properties.senderId())) {
            return SmsSendResult.failure(null, "Notify.lk SMS credentials are not configured");
        }

        String normalizedPhoneNumber = normalizePhoneNumber(phoneNumber);
        if (!StringUtils.hasText(normalizedPhoneNumber)) {
            return SmsSendResult.failure(null, "Phone number could not be normalized for Notify.lk");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("user_id", properties.userId());
        formData.add("api_key", properties.apiKey());
        formData.add("sender_id", properties.senderId());
        formData.add("to", normalizedPhoneNumber);
        formData.add("message", message);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(ENDPOINT, request, String.class);
            String body = response.getBody();
            log.info("Notify.lk SMS response for {} (normalized from {}): status={}, body={}", normalizedPhoneNumber,
                    phoneNumber, response.getStatusCode(), body);

            if (response.getStatusCode().is2xxSuccessful()) {
                return SmsSendResult.success(body);
            }

            return SmsSendResult.failure(body, "Notify.lk returned HTTP " + response.getStatusCode().value());
        } catch (HttpStatusCodeException ex) {
            String responseBody = ex.getResponseBodyAsString();
            log.warn("Notify.lk SMS failed for {} (normalized from {}): status={}, body={}", normalizedPhoneNumber,
                    phoneNumber, ex.getStatusCode(), responseBody);
            return SmsSendResult.failure(responseBody, ex.getMessage());
        } catch (RestClientException ex) {
            log.warn("Notify.lk SMS failed for {} (normalized from {})", normalizedPhoneNumber, phoneNumber, ex);
            return SmsSendResult.failure(null, ex.getMessage());
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return null;
        }

        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        if (digitsOnly.startsWith("94") && digitsOnly.length() == 11) {
            return digitsOnly;
        }

        if (digitsOnly.startsWith("0") && digitsOnly.length() == 10) {
            return "94" + digitsOnly.substring(1);
        }

        if (digitsOnly.startsWith("7") && digitsOnly.length() == 9) {
            return "94" + digitsOnly;
        }

        if (digitsOnly.length() == 11) {
            return digitsOnly;
        }

        return null;
    }
}