package com.example.accounts.config;

import com.example.accounts.dto.TransferCompletedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferCompletedEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, TransferCompletedEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, TransferCompletedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3); // scale reads
        return factory;
    }
}

