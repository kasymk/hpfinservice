package com.example.transfer.fraud;

import com.example.transfer.fraud.FraudDecision;

public record FraudCheckResponse(
        FraudDecision decision,
        String reason
) {}
