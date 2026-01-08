package com.example.transfer.outbox;

import com.example.transfer.events.TransferCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {

        List<OutboxEvent> events =
                outboxRepository.findBatchForProcessing(
                        PageRequest.of(0, 50)
                );

        for (OutboxEvent event : events) {
            try {
                TransferCompletedEvent domainEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                TransferCompletedEvent.class
                        );

                kafkaTemplate.send(
                        "transfer.notifications",
                        event.getAggregateId().toString(),
                        domainEvent
                ).get();

                event.setStatus(OutboxStatus.SENT.getValue());
                event.setSentAt(Instant.now());

            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED.getValue());
            }
        }
    }
}

