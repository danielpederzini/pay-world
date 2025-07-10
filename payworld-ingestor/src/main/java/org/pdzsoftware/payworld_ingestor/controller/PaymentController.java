package org.pdzsoftware.payworld_ingestor.controller;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.publisher.EventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final EventPublisher eventPublisher;

    @PostMapping
    public Flux<Void> postPayment(@RequestBody RawPaymentDTO body) {
        eventPublisher.publish(body.getUuid(), body);
        return Flux.empty();
    }
}
