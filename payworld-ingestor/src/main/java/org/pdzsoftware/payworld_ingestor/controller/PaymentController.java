package org.pdzsoftware.payworld_ingestor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.entity.Payment;
import org.pdzsoftware.payworld_ingestor.entity.PaymentStatus;
import org.pdzsoftware.payworld_ingestor.publisher.EventPublisher;
import org.pdzsoftware.payworld_ingestor.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public Mono<Void> postPayment(@RequestBody RawPaymentDTO body) {
        log.info("[PaymentController] Received payment request with key: {}", body.getUuid());
        return paymentService.process(body);

    }
}
