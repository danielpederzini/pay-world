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

    public void publish(String key, EnrichedPaymentDTO message) {
        CompletableFuture<SendResult<String, EnrichedPaymentDTO>> future = kafkaTemplate.send(topic, key, message);

        future.thenAccept(result -> {
            log.info("[EventPublisher] Sent message with key: {}", key);
        }).exceptionally(ex -> {
            String error = "[EventPublisher] Error sending message with key: {}";
            log.error(error, key, ex);
            throw new RuntimeException(error, ex);
        });
    }
}
