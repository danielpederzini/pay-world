package org.pdzsoftware.payworld_payment_processor.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.pdzsoftware.payworld_payment_processor.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_payment_processor.exception.AcknowledgeableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaDlqConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, EnrichedPaymentDTO> dlqProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, "false");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EnrichedPaymentDTO> dlqTemplate(ProducerFactory<String, EnrichedPaymentDTO> dlqProducerFactory) {
        return new KafkaTemplate<>(dlqProducerFactory);
    }

    @Bean
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, EnrichedPaymentDTO> dlqTemplate) {
        return new DeadLetterPublishingRecoverer(dlqTemplate, (rec, ex) -> {
            if (ex.getCause() instanceof AcknowledgeableException) {
                log.warn("[DeadLetterPublishingRecoverer] Couldn't complete payment with key: {}, reason: {}",
                        rec.key(), ex.getCause().getMessage());
                return null;
            } else {
                log.error("[DeadLetterPublishingRecoverer] Error handling record with key: {}",
                        rec.key(), ex);
                return new TopicPartition(rec.topic() + ".dlq", rec.partition());
            }
        });
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(AcknowledgeableException.class);
        return handler;
    }
}
