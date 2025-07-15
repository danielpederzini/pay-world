package org.pdzsoftware.payworld_ingestor.controller;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.publisher.EventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final EventPublisher eventPublisher;

    @PostMapping
    public Mono<Void> postPayment(@RequestBody RawPaymentDTO body) {
        return eventPublisher.publish(body.getUuid(), body);
    }
}
