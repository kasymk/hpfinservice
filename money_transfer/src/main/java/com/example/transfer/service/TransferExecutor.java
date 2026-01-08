package com.example.transfer.service;

import com.example.transfer.domain.Account;
import com.example.transfer.domain.IdempotencyKey;
import com.example.transfer.domain.LedgerEntry;
import com.example.transfer.dto.TransferRequest;
import com.example.transfer.events.TransferCompletedEvent;
import com.example.transfer.exception.BusinessException;
import com.example.transfer.outbox.OutboxEvent;
import com.example.transfer.outbox.OutboxRepository;
import com.example.transfer.outbox.OutboxStatus;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.repository.IdempotencyRepository;
import com.example.transfer.repository.LedgerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferExecutor {
    private final AccountRepository accountRepo;
    private final LedgerRepository ledgerRepo;
    private final IdempotencyRepository idemRepo;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public TransferExecutor(
            AccountRepository accountRepo,
            LedgerRepository ledgerRepo,
            IdempotencyRepository idemRepo,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.accountRepo = accountRepo;
        this.ledgerRepo = ledgerRepo;
        this.idemRepo = idemRepo;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = Exception.class
    )
    public void performTransfer(UUID idempotencyKey, TransferRequest req, UUID transferId) {
        // 1️⃣ Idempotency check
        if (idemRepo.existsById(idempotencyKey)) {
            return;
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

        createOutboxEvent(transferId, req, from, to);
    }

    private void createOutboxEvent(UUID transferId, TransferRequest req, Account from, Account to) {
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setAggregateType("TRANSFER");
        event.setAggregateId(transferId);
        event.setEventType("TRANSFER_COMPLETED");
        event.setPayload(createPayload(transferId, req, from, to, event.getId()));
        event.setStatus(OutboxStatus.NEW.getValue());
        event.setCreatedAt(Instant.now());

        outboxRepository.save(event);
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
            TransferRequest req,
            Account from, Account to, UUID eventId) {
        try {
            TransferCompletedEvent event =
                    new TransferCompletedEvent(
                            eventId,
                            transferId,
                            req.getFromAccount(),
                            from.getClientId(),
                            req.getToAccount(),
                            to.getClientId(),
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
