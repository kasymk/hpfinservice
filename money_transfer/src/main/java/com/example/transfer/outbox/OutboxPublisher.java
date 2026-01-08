package com.example.transfer.outbox;

import com.example.transfer.events.TransferCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {
    private final ObjectMapper objectMapper;
    private final TransferNotificationPublisher notificationPublisher;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxPublisher(
            ObjectMapper objectMapper,
            OutboxRepository outboxRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            TransferNotificationPublisher notificationPublisher
    ) {
        this.objectMapper = objectMapper;
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.notificationPublisher = notificationPublisher;
    }
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
                        "transfer.completed",
                        event.getAggregateId().toString(),
                        domainEvent
                ).get();

                event.setStatus(OutboxStatus.SENT.getValue());
                event.setSentAt(Instant.now());

                notificationPublisher.publish(event);
            } catch (Exception ex) {
                event.setStatus(OutboxStatus.FAILED.getValue());
            }
        }
    }
}

