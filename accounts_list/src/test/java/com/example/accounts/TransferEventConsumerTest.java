package com.example.accounts;

import com.example.accounts.dto.TransferCompletedEvent;
import com.example.accounts.entity.AccountView;
import com.example.accounts.repository.AccountViewRepository;
import com.example.accounts.repository.ProcessedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class TransferEventConsumerTest extends IntegrationTestBase {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AccountViewRepository accountRepo;

    @Autowired
    private ProcessedEventRepository processedRepo;

    @BeforeEach
    void setup() {
        accountRepo.deleteAll();
        processedRepo.deleteAll();

        accountRepo.save(new AccountView(
                "acc-1",
                "client-A",
                "USD",
                new BigDecimal("1000.00")
        ));

        accountRepo.save(new AccountView(
                "acc-2",
                "client-B",
                "USD",
                new BigDecimal("1000.00")
        ));
    }
    @Test
    void shouldConsumeTransferAndUpdateAccounts() {
        TransferCompletedEvent event = validEvent();

        kafkaTemplate.send("transfer.completed", event);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            AccountView from = accountRepo.findById("acc-1").orElseThrow();
            AccountView to = accountRepo.findById("acc-2").orElseThrow();

            assertThat(from.getBalance()).isEqualByComparingTo("900.00");
            assertThat(to.getBalance()).isEqualByComparingTo("1100.00");
            assertThat(processedRepo.existsById(event.getEventId())).isTrue();
        });
    }

    @Test
    void shouldBeIdempotent() {
        TransferCompletedEvent event = validEvent();

        kafkaTemplate.send("transfer.completed", event);
        kafkaTemplate.send("transfer.completed", event); // duplicate

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            AccountView from = accountRepo.findById("acc-1").orElseThrow();
            assertThat(from.getBalance()).isEqualByComparingTo("900.00");
        });
    }

    private TransferCompletedEvent validEvent() {
        TransferCompletedEvent e = new TransferCompletedEvent();
        e.setEventId(UUID.randomUUID().toString());
        e.setTransferId("tx-1");
        e.setFromAccountId("acc-1");
        e.setFromClientId("client-A");
        e.setToAccountId("acc-2");
        e.setToClientId("client-B");
        e.setAmount(new BigDecimal("100.00"));
        e.setCurrency("USD");
        e.setOccurredAt(Instant.now());
        return e;
    }
}
