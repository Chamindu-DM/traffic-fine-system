package com.trafficfine.config;

import com.trafficfine.sms.MockSmsProvider;
import com.trafficfine.sms.NotifyLkSmsProvider;
import com.trafficfine.sms.SmsProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean(name = "mockSmsProvider")
    public SmsProvider mockSmsProvider() {
        return new MockSmsProvider();
    }

    @Bean(name = "smsProvider")
    @ConditionalOnProperty(name = "sms.provider", havingValue = "notifylk")
    public SmsProvider notifyLkSmsProvider(RestTemplate restTemplate, SmsProperties smsProperties) {
        return new NotifyLkSmsProvider(restTemplate, smsProperties);
    }

    @Bean(name = "smsProvider")
    @ConditionalOnProperty(name = "sms.provider", havingValue = "mock", matchIfMissing = true)
    public SmsProvider mockPrimarySmsProvider(@Qualifier("mockSmsProvider") SmsProvider mockSmsProvider) {
        return mockSmsProvider;
    }
}