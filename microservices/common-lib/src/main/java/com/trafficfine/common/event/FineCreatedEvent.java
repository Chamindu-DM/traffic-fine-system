package com.trafficfine.common.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FineCreatedEvent(
        @JsonProperty("referenceNumber") String referenceNumber,
        @JsonProperty("categoryCode") String categoryCode,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("district") String district,
        @JsonProperty("officerName") String officerName,
        @JsonProperty("issuedAt") LocalDateTime issuedAt
) implements Serializable {}
