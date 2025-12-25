package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InsufficientFundsTest extends BaseIntegrationTest {

    @Test
    void insufficientFundsMustRollback() throws Exception {

        UUID from = createAccount("USD", new BigDecimal("50.00"));
        UUID to   = createAccount("USD", BigDecimal.ZERO);

        TransferRequest req = new TransferRequest();
        req.setFromAccount(from);
        req.setToAccount(to);
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency("USD");

        mockMvc.perform(post("/transfers")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        assertEquals(new BigDecimal("50.00"), balance(from));
        assertEquals(BigDecimal.ZERO, balance(to));
    }
}

