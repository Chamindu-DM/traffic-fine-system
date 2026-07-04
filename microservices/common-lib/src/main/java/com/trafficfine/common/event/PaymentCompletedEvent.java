package com.trafficfine.common.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentCompletedEvent(
        @JsonProperty("paymentReference") String paymentReference,
        @JsonProperty("fineReferenceNumber") String fineReferenceNumber,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("officerPhoneNumber") String officerPhoneNumber,
        @JsonProperty("officerName") String officerName,
        @JsonProperty("paidAt") LocalDateTime paidAt
) implements Serializable {}
