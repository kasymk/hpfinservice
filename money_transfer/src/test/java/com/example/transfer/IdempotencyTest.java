package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IdempotencyTest extends BaseIntegrationTest {

    @Test
    void sameIdempotencyKeyMustNotDuplicateTransfer() throws Exception {

        UUID from = createAccount("USD", new BigDecimal("300.00"));
        UUID to   = createAccount("USD", BigDecimal.ZERO);
        UUID key  = UUID.randomUUID();

        TransferRequest req = new TransferRequest();
        req.setFromAccount(from);
        req.setToAccount(to);
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency("USD");

        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/transfers")
                            .header("Idempotency-Key", key)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isAccepted());
        }

        assertEquals(new BigDecimal("200.00"), balance(from));
        assertEquals(new BigDecimal("100.00"), balance(to));
    }
}
