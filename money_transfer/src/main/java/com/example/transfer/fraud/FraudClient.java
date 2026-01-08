package com.example.transfer.fraud;

import com.example.transfer.fraud.FraudCheckRequest;
import com.example.transfer.fraud.FraudCheckResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class FraudClient {

    private final WebClient webClient;

    public FraudClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://fraud-service")
                .build();
    }

    public FraudCheckResponse check(FraudCheckRequest request) {

        return webClient.post()
                .uri("/api/fraud/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FraudCheckResponse.class)
                .timeout(Duration.ofMillis(80))   // HARD timeout
                .block();
    }
}

