package com.example.transfer.outbox;

import com.example.transfer.events.TransferCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferNotificationPublisher {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransferNotificationPublisher(
            ObjectMapper objectMapper,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OutboxEvent event) {
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
            ).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn(
                            "Failed to publish notification for event {}",
                            event.getId(),
                            ex
                    );
                }
            });

        } catch (Exception ex) {
            log.warn("Failed to deserialize notification event {}", event.getId(), ex);
        }
    }
}
