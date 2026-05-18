package com.trafficfine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "officers")
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String badgeNumber;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String district;

    protected Officer() {
    }

    public Officer(String name, String badgeNumber, String phoneNumber, String district) {
        this.name = name;
        this.badgeNumber = badgeNumber;
        this.phoneNumber = phoneNumber;
        this.district = district;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBadgeNumber() {
        return badgeNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDistrict() {
        return district;
    }
}
