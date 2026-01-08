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

class TransferControllerTest extends BaseIntegrationTest{

    @Test
    void shouldAcceptTransferRequest() throws Exception {
        UUID fromId = createAccount("USD", new BigDecimal("500.00"));
        UUID toId   = createAccount("USD", BigDecimal.ZERO);

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
