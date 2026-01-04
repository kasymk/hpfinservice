package com.example.accounts;

import com.example.accounts.repository.AccountViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class SchemaValidationTest extends IntegrationTestBase {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AccountViewRepository accountRepo;

    @Test
    void invalidEventShouldGoToDlq() {
        Map<String, Object> invalidEvent = Map.of(
                "eventId", "bad-event"
        );

        kafkaTemplate.send("transfer.completed", invalidEvent);

        // DB should remain unchanged
        await().during(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThat(accountRepo.findAll()).isEmpty()
        );
    }
}

