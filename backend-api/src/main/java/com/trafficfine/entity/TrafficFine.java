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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_fines")
public class TrafficFine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private FineCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "officer_id")
    private Officer officer;

    @Column(nullable = false)
    private String driverLicenseNumber;

    @Column(nullable = false)
    private String vehicleNumber;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    private LocalDateTime paidAt;

    protected TrafficFine() {
    }

    public TrafficFine(
            String referenceNumber,
            FineCategory category,
            Officer officer,
            String driverLicenseNumber,
            String vehicleNumber,
            String district,
            FineStatus status,
            LocalDateTime issuedAt
    ) {
        this.referenceNumber = referenceNumber;
        this.category = category;
        this.officer = officer;
        this.driverLicenseNumber = driverLicenseNumber;
        this.vehicleNumber = vehicleNumber;
        this.district = district;
        this.amount = category.getAmount();
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public FineCategory getCategory() {
        return category;
    }

    public Officer getOfficer() {
        return officer;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getDistrict() {
        return district;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public FineStatus getStatus() {
        return status;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void markPaid(LocalDateTime paidAt) {
        this.status = FineStatus.PAID;
        this.paidAt = paidAt;
    }
}
