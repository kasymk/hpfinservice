package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceConsistencyTest extends BaseIntegrationTest {

    @Test
    void totalMoneyMustRemainConstant() throws Exception {

        UUID a = createAccount("USD", new BigDecimal("500.00"));
        UUID b = createAccount("USD", BigDecimal.ZERO);

        TransferRequest req = new TransferRequest();
        req.setFromAccount(a);
        req.setToAccount(b);
        req.setAmount(new BigDecimal("200.00"));
        req.setCurrency("USD");

        mockMvc.perform(post("/transfers")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());

        BigDecimal total = balance(a).add(balance(b));
        assertEquals(new BigDecimal("500.00"), total);
    }
}

