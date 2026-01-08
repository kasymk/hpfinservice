package com.example.transfer.outbox;

import com.example.transfer.outbox.OutboxEvent;
import com.example.transfer.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {

        List<OutboxEvent> events =
                outboxRepository.findBatchForProcessing(
                        PageRequest.of(0, 50)
                );

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        "transfer.notifications",
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get();

                event.setStatus("SENT");
                event.setSentAt(Instant.now());

            } catch (Exception ex) {
                event.setStatus("FAILED");
            }
        }
    }
}

