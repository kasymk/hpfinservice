package com.example.transfer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ledger_entry",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"transferId", "accountId"}
        )
)
@Getter
@Setter
public class LedgerEntry {

    @Id
    private UUID id;

    private UUID accountId;

    private UUID transferId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    private Instant createdAt;
}

