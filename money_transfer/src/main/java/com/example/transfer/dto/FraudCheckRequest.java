package com.example.transfer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudCheckRequest(
        UUID transferId,
        UUID fromAccount,
        UUID toAccount,
        BigDecimal amount,
        String currency
) {}

