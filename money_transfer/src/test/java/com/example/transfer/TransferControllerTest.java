package com.example.transfer;

import com.example.transfer.domain.Account;
import com.example.transfer.domain.LedgerEntry;
import com.example.transfer.dto.TransferRequest;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.repository.LedgerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Test
    void shouldAcceptTransferRequest() throws Exception {

        // 1️⃣ Create accounts
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();

        Account from = new Account();
        from.setId(fromId);
        from.setCurrency("USD");
        from.setStatus("ACTIVE");
        from.setClientId(UUID.randomUUID());

        Account to = new Account();
        to.setId(toId);
        to.setCurrency("USD");
        to.setStatus("ACTIVE");
        to.setClientId(UUID.randomUUID());

        accountRepository.save(from);
        accountRepository.save(to);

        // 2️⃣ Give initial balance
        LedgerEntry initial = new LedgerEntry();
        initial.setId(UUID.randomUUID());
        initial.setAccountId(fromId);
        initial.setTransferId(UUID.randomUUID());
        initial.setAmount(new BigDecimal("500.00"));
        initial.setCreatedAt(Instant.now());

        ledgerRepository.save(initial);

        // 3️⃣ Call API
        TransferRequest request = new TransferRequest();
        request.setFromAccount(fromId);
        request.setToAccount(toId);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");

        mockMvc.perform(
                        post("/transfers")
                                .header("Idempotency-Key", UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isAccepted());
    }
}
