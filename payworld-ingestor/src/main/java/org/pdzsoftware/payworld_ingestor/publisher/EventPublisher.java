package org.pdzsoftware.payworld_ingestor.publisher;

import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EventPublisher {
    private final KafkaTemplate<String, RawPaymentDTO> kafkaTemplate;
    private final String topic;

    public EventPublisher(KafkaTemplate<String, RawPaymentDTO> kafkaTemplate,
                          @Value("${app.topic.produce}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(String key, RawPaymentDTO message) {
        CompletableFuture<SendResult<String, RawPaymentDTO>> future = kafkaTemplate.send(topic, key, message);

        future.thenAccept(result -> {
            log.info("Sent message with key: {}", key);
        }).exceptionally(ex -> {
            String error = "Error sending message with key: {}";
            log.error(error, key, ex);
            throw new RuntimeException(error, ex);
        });
    }
}
