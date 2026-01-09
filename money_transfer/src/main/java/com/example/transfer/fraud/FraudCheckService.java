package com.example.transfer.fraud;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class FraudCheckService {

    private final FraudClientRest client;

    public FraudCheckService(FraudClientRest client) {
        this.client = client;
    }

    @CircuitBreaker(
            name = "fraudService",
            fallbackMethod = "fallback"
    )
    public FraudDecision check(FraudCheckRequest request) {

        FraudCheckResponse response = client.check(request);
        return response.getDecision();
    }

    public FraudDecision fallback(
            FraudCheckRequest request,
            Throwable ex
    ) {
        return FraudDecision.REVIEW;
    }
}

