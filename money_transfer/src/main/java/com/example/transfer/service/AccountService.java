package com.example.transfer.service;

import com.example.transfer.domain.Account;
import com.example.transfer.domain.LedgerEntry;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepo;

    @Transactional
    public UUID createAccount(UUID clientId, String currency, BigDecimal initialBalance) {

        UUID accountId = createNewAccount(clientId, currency);
        createInitialBalance(accountId, initialBalance);
        return accountId;
    }

    private void createInitialBalance(UUID accountId, BigDecimal initialBalance) {
        LedgerEntry e = new LedgerEntry();
        e.setId(UUID.randomUUID());
        e.setAccountId(accountId);
        e.setTransferId(UUID.randomUUID());
        e.setAmount(initialBalance);
        e.setCreatedAt(Instant.now());
        ledgerRepo.save(e);
    }

    private UUID createNewAccount(UUID clientId, String currency) {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setId(accountId);
        account.setClientId(clientId);
        account.setCurrency(currency);
        account.setStatus("ACTIVE");

        accountRepository.save(account);
        return accountId;
    }
}

