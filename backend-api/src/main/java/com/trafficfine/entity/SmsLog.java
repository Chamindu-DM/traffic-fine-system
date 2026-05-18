package com.trafficfine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_logs")
public class SmsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fine_id")
    private TrafficFine fine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "officer_id")
    private Officer officer;

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

    public SmsLog(TrafficFine fine, Officer officer, String phoneNumber, String message, SmsStatus status, LocalDateTime sentAt) {
        this.fine = fine;
        this.officer = officer;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public TrafficFine getFine() {
        return fine;
    }

    public Officer getOfficer() {
        return officer;
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
