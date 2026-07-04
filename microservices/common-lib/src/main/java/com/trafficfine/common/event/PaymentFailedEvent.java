package com.trafficfine.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentFailedEvent(
        @JsonProperty("fineReferenceNumber") String fineReferenceNumber,
        @JsonProperty("reason") String reason,
        @JsonProperty("failedAt") LocalDateTime failedAt
) implements Serializable {}
