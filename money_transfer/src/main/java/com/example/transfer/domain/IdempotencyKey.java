package com.example.transfer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_key")
@Getter
@Setter
public class IdempotencyKey {

    @Id
    private UUID key;

    private Instant createdAt;
}

