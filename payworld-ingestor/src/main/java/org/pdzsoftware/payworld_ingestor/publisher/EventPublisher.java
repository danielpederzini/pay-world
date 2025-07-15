package org.pdzsoftware.payworld_ingestor.publisher;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Service
public class EventPublisher {
    private final KafkaSender<String, RawPaymentDTO> kafkaSender;
    private final String topic;

    public EventPublisher(KafkaSender<String, RawPaymentDTO> kafkaTemplate,
                          @Value("${app.topic.produce}") String topic) {
        this.kafkaSender = kafkaTemplate;
        this.topic = topic;
    }

    public Mono<Void> publish(String key, RawPaymentDTO message) {
        SenderRecord<String, RawPaymentDTO, String> record =
                SenderRecord.create(new ProducerRecord<>(topic, key, message), key);

        return kafkaSender.send(Mono.just(record))
                .doOnError(e -> log.error("[EventPublisher] Error sending message with key: {}", key, e))
                .then();
    }
}
