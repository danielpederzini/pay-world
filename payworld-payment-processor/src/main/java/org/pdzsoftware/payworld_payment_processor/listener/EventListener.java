package org.pdzsoftware.payworld_payment_processor.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_payment_processor.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_payment_processor.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventListener {
    private final PaymentService paymentService;

    @KafkaListener(topics = "${app.topic.consume}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(EnrichedPaymentDTO message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("[EventListener] Received message with key: {}", key);
        paymentService.processPayment(message);
    }
}
