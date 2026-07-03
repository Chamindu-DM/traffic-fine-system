package com.trafficfine.paymentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fineReferenceNumber;

    @Column(nullable = false, unique = true)
    private String paymentReference;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column(unique = true)
    private String idempotencyKey;

    protected Payment() {
    }

    public Payment(String fineReferenceNumber, String paymentReference, BigDecimal amount, String paymentMethod, PaymentStatus status, LocalDateTime paidAt, String idempotencyKey) {
        this.fineReferenceNumber = fineReferenceNumber;
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paidAt = paidAt;
        this.idempotencyKey = idempotencyKey;
    }

    public Long getId() {
        return id;
    }

    public String getFineReferenceNumber() {
        return fineReferenceNumber;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
