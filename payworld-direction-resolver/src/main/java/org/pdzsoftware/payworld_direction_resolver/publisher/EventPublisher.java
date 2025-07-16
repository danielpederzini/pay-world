package org.pdzsoftware.payworld_direction_resolver.publisher;

import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_direction_resolver.dto.EnrichedPaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EventPublisher {
    private final KafkaTemplate<String, EnrichedPaymentDTO> kafkaTemplate;
    private final String topic;

    public EventPublisher(KafkaTemplate<String, EnrichedPaymentDTO> kafkaTemplate,
                          @Value("${app.topic.produce}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<String, EnrichedPaymentDTO>> publish(String key, EnrichedPaymentDTO message) {
        return kafkaTemplate.send(topic, key, message);
    }
}
