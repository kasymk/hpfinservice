package com.example.transfer.dto;

public record FraudCheckResponse(
        FraudDecision decision,
        String reason
) {}
