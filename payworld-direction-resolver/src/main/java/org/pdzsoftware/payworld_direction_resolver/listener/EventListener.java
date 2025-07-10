package org.pdzsoftware.payworld_direction_resolver.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_direction_resolver.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_direction_resolver.service.PaymentService;
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
    public void onMessage(RawPaymentDTO message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received message with key: {}", key);
        paymentService.processPayment(message);
    }
}
