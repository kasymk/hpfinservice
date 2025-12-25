package com.example.transfer;

import com.example.transfer.domain.Account;
import com.example.transfer.domain.LedgerEntry;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.repository.LedgerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AccountRepository accountRepo;

    @Autowired
    protected LedgerRepository ledgerRepo;

    protected UUID createAccount(String currency, BigDecimal initialBalance) {
        UUID id = UUID.randomUUID();

        Account acc = new Account();
        acc.setId(id);
        acc.setCurrency(currency);
        acc.setStatus("ACTIVE");
        accountRepo.save(acc);

        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            LedgerEntry e = new LedgerEntry();
            e.setId(UUID.randomUUID());
            e.setAccountId(id);
            e.setTransferId(UUID.randomUUID());
            e.setAmount(initialBalance);
            e.setCreatedAt(Instant.now());
            ledgerRepo.save(e);
        }

        return id;
    }

    protected BigDecimal balance(UUID accountId) {
        return ledgerRepo.calculateBalance(accountId);
    }
}
