package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.service.TransferPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class RetryIntegrationTest {

    @Autowired
    private TransferPostProcessor postProcessor;

    @Test
    void shouldRetryOnTemporaryFailureAndEventuallySucceed() {
        TransferRequest req = new TransferRequest();
        req.setFromAccount(UUID.randomUUID());
        req.setToAccount(UUID.randomUUID());
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency("USD");

        // when + then (must NOT throw after retries)
        assertDoesNotThrow(() ->
                postProcessor.handlePostTransfer(req)
        );

        // verify retry count
        assertEquals(3, postProcessor.attempts(),
                "Expected method to be called 3 times due to retries");
    }
}
