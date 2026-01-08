package com.example.transfer.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID eventId,
        UUID transferId,
        UUID fromAccount,
        UUID fromClientId,
        UUID toAccount,
        UUID toClientId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {}
