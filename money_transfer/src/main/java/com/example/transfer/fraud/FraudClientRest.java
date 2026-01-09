package com.example.transfer.fraud;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FraudClientRest {

    private final RestClient restClient;

    public FraudClientRest(RestClient fraudRestClient) {
        this.restClient = fraudRestClient;
    }

    public FraudCheckResponse check(FraudCheckRequest request) {

        return restClient.post()
                .uri("/api/fraud/check")
                .body(request)
                .retrieve()
                .body(FraudCheckResponse.class);
    }
}

