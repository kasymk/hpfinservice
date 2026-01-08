package com.example.transfer.fraud;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class FraudClient {

    private final WebClient webClient;
    private final RestClient restClient;

    public FraudClient(WebClient.Builder builder, RestClient.Builder restClientBuilder) {
        this.webClient = builder
                .baseUrl("http://localhost:8082")
                .build();
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8082")
                .build();;
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

    public FraudCheckResponse checkRest(FraudCheckRequest request) {

        return restClient.post()
                .uri("/api/fraud/check")
                .body(request)
                .retrieve()
                .body(FraudCheckResponse.class);
    }
}

