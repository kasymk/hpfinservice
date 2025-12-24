package com.example.transfer.repository;

import com.example.transfer.domain.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotencyRepository
        extends JpaRepository<IdempotencyKey, UUID> {
}

