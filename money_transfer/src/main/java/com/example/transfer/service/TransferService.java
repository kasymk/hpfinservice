package com.example.transfer.service;

import com.example.transfer.domain.*;
import com.example.transfer.dto.TransferRequest;
import com.example.transfer.events.TransferCompletedEvent;
import com.example.transfer.exception.BusinessException;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.repository.IdempotencyRepository;
import com.example.transfer.repository.LedgerRepository;
import com.example.transfer.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepo;
    private final LedgerRepository ledgerRepo;
    private final IdempotencyRepository idemRepo;
    private final OutboxRepository outboxRepository;
    private final TransferPostProcessor postProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = Exception.class
    )
    public void transfer(UUID idempotencyKey, TransferRequest req) {
        UUID transferId = performTransfer(idempotencyKey, req);
        if (transferId == null) return;

        postProcessor.handlePostTransfer(req);
        createOutboxEvent(transferId, req);
    }

    private void createOutboxEvent(UUID transferId, TransferRequest req) {
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateType("TRANSFER");
        event.setAggregateId(transferId);
        event.setEventType("TRANSFER_COMPLETED");
        event.setPayload(createPayload(transferId, req));
        event.setStatus("NEW");
        event.setCreatedAt(Instant.now());

        outboxRepository.save(event);
    }

    private UUID performTransfer(UUID idempotencyKey, TransferRequest req) {
        // 1️⃣ Idempotency check
        if (idemRepo.existsById(idempotencyKey)) {
            return null;
        }

        // 2️⃣ Lock accounts (prevents double spend)
        Account from = accountRepo.lockById(req.getFromAccount());
        if (from == null) {
            throw new BusinessException("Source account not found");
        }
        Account to = accountRepo.lockById(req.getToAccount());
        if (to == null) {
            throw new BusinessException("Target account not found");
        }

        BigDecimal balance =
                ledgerRepo.calculateBalance(from.getId());

        if (balance.compareTo(req.getAmount()) < 0) {
            throw new BusinessException("Insufficient funds");
        }

        UUID transferId = UUID.randomUUID();

        // 3️⃣ Debit
        ledgerRepo.save(entry(
                from.getId(),
                transferId,
                req.getAmount().negate()
        ));

        // 4️⃣ Credit
        ledgerRepo.save(entry(
                to.getId(),
                transferId,
                req.getAmount()
        ));

        // 5️⃣ Store idempotency key
        IdempotencyKey key = new IdempotencyKey();
        key.setId(idempotencyKey);
        key.setCreatedAt(Instant.now());
        idemRepo.save(key);

        return transferId;
    }

    private LedgerEntry entry(UUID accountId,
                              UUID transferId,
                              BigDecimal amount) {
        LedgerEntry e = new LedgerEntry();
        e.setId(UUID.randomUUID());
        e.setAccountId(accountId);
        e.setTransferId(transferId);
        e.setAmount(amount);
        e.setCreatedAt(Instant.now());
        return e;
    }

    private String createPayload(
            UUID transferId,
            TransferRequest req
    ) {
        try {
            TransferCompletedEvent event =
                    new TransferCompletedEvent(
                            transferId,
                            req.getFromAccount(),
                            req.getToAccount(),
                            req.getAmount(),
                            req.getCurrency(),
                            Instant.now()
                    );

            return objectMapper.writeValueAsString(event);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize transfer event", e
            );
        }
    }

}

