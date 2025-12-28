package com.example.transfer.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID transferId,
        UUID fromAccount,
        UUID toAccount,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {}
