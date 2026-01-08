package com.example.fraud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Fraud check request")
public class FraudCheckRequest {

        @Schema(
                description = "Unique transfer identifier",
                example = "11111111-1111-1111-1111-111111111111",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private UUID transferId;

        @Schema(
                description = "Source account identifier",
                example = "22222222-2222-2222-2222-222222222222",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private UUID fromAccount;

        @Schema(
                description = "Destination account identifier",
                example = "33333333-3333-3333-3333-333333333333",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private UUID toAccount;

        @Schema(
                description = "Transfer amount",
                example = "100.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private BigDecimal amount;

        @Schema(
                description = "ISO 4217 currency code",
                example = "USD",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String currency;

        public FraudCheckRequest() {
        }

        public UUID getTransferId() {
                return transferId;
        }

        public void setTransferId(UUID transferId) {
                this.transferId = transferId;
        }

        public UUID getFromAccount() {
                return fromAccount;
        }

        public void setFromAccount(UUID fromAccount) {
                this.fromAccount = fromAccount;
        }

        public UUID getToAccount() {
                return toAccount;
        }

        public void setToAccount(UUID toAccount) {
                this.toAccount = toAccount;
        }

        public BigDecimal getAmount() {
                return amount;
        }

        public void setAmount(BigDecimal amount) {
                this.amount = amount;
        }

        public String getCurrency() {
                return currency;
        }

        public void setCurrency(String currency) {
                this.currency = currency;
        }
}
