package com.trafficfine.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public final class FallbackController {

    @GetMapping("/payment")
    public Mono<ResponseEntity<String>> paymentFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment Service is taking longer than expected. Please try again."));
    }

    @GetMapping("/fine")
    public Mono<ResponseEntity<String>> fineFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Fine Service is currently unavailable. Please try again later."));
    }
}
