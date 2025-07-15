package org.pdzsoftware.payworld_payment_processor.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_payment_processor.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_payment_processor.exception.AcknowledgeableException;
import org.pdzsoftware.payworld_payment_processor.service.PaymentProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventListener {
    private final PaymentProcessor paymentProcessor;

    @KafkaListener(topics = "${app.topic.consume}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(EnrichedPaymentDTO message, @Header(KafkaHeaders.RECEIVED_KEY) String key, Acknowledgment ack) {
        log.info("Received message with key: {}", key);

        try {
            paymentProcessor.process(message);
            ack.acknowledge();
        } catch (AcknowledgeableException e) {
            log.warn("[EventListener] Acknowledgeable error processing payment: {}", e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[EventListener] Unexpected error processing payment: {}", e.getMessage());
            throw e;
        }
    }
}
