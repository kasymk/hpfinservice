package com.example.transfer.fraud;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Fraud check response")
public class FraudCheckResponse {

    @Schema(
            description = "Fraud decision result",
            example = "APPROVE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private FraudDecision decision;

    @Schema(
            description = "Reason for the decision",
            example = "Automatically approved",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String reason;

    public FraudCheckResponse() {
    }

    public FraudCheckResponse(FraudDecision decision, String reason) {
        this.decision = decision;
        this.reason = reason;
    }

    public FraudDecision getDecision() {
        return decision;
    }

    public void setDecision(FraudDecision decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

