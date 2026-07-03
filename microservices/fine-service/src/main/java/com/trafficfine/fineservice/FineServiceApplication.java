package com.trafficfine.fineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FineServiceApplication.class, args);
    }
}
