package com.trafficfine.common.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FineStatusChangedEvent(
        @JsonProperty("referenceNumber") String referenceNumber,
        @JsonProperty("oldStatus") String oldStatus,
        @JsonProperty("newStatus") String newStatus,
        @JsonProperty("changedAt") LocalDateTime changedAt
) implements Serializable {}
