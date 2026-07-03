package com.trafficfine.reportingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_fine_reports")
public class TrafficFineReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private String categoryCode;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String officerName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime paidAt;

    protected TrafficFineReport() {
    }

    public TrafficFineReport(String referenceNumber, String categoryCode, String categoryName, BigDecimal amount, String district, String officerName, String status, LocalDateTime issuedAt) {
        this.referenceNumber = referenceNumber;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.amount = amount;
        this.district = district;
        this.officerName = officerName;
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDistrict() {
        return district;
    }

    public String getOfficerName() {
        return officerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
