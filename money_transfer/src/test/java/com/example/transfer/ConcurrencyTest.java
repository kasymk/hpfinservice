package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class ConcurrencyTest extends BaseIntegrationTest {

    @Test
    void concurrentTransfersMustNotOverdraw() throws Exception {

        UUID from = createAccount("USD", new BigDecimal("100.00"));
        UUID to   = createAccount("USD", BigDecimal.ZERO);

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    TransferRequest req = new TransferRequest();
                    req.setFromAccount(from);
                    req.setToAccount(to);
                    req.setAmount(new BigDecimal("20.00"));
                    req.setCurrency("USD");

                    mockMvc.perform(post("/transfers")
                            .header("Idempotency-Key", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)));
                } catch (Exception ignored) {}
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        BigDecimal fromBalance = balance(from);
        BigDecimal toBalance = balance(to);

        assertTrue(fromBalance.compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(new BigDecimal("100.00"), fromBalance.add(toBalance));
    }
}

