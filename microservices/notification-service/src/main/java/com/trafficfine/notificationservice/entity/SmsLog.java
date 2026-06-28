package com.trafficfine.notificationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_logs")
public class SmsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fineReferenceNumber;

    @Column(nullable = false)
    private String officerName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SmsStatus status;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    protected SmsLog() {
    }

    public SmsLog(String fineReferenceNumber, String officerName, String phoneNumber, String message, SmsStatus status, LocalDateTime sentAt) {
        this.fineReferenceNumber = fineReferenceNumber;
        this.officerName = officerName;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public String getFineReferenceNumber() {
        return fineReferenceNumber;
    }

    public String getOfficerName() {
        return officerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public SmsStatus getStatus() {
        return status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
