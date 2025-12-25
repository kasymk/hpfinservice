package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountNotFoundTest extends BaseIntegrationTest {

    @Test
    void shouldFailWhenAccountDoesNotExist() throws Exception {

        TransferRequest req = new TransferRequest();
        req.setFromAccount(UUID.randomUUID());
        req.setToAccount(UUID.randomUUID());
        req.setAmount(new BigDecimal("10.00"));
        req.setCurrency("USD");

        mockMvc.perform(post("/transfers")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
