package org.pdzsoftware.payworld_ingestor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.publisher.EventPublisher;
import org.pdzsoftware.payworld_ingestor.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;

    @PostMapping
    public Mono<Void> postPayment(@RequestBody RawPaymentDTO body) {
        log.info("[EventPublisher] Received payment request with key: {}", body.getUuid());
        return paymentService.persistPayment(body).then(eventPublisher.publish(body.getUuid(), body));
    }
}
