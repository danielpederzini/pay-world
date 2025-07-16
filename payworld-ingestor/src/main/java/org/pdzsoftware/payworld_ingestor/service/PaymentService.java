package org.pdzsoftware.payworld_ingestor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.entity.Payment;
import org.pdzsoftware.payworld_ingestor.entity.PaymentStatus;
import org.pdzsoftware.payworld_ingestor.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Mono<Payment> persistPayment(RawPaymentDTO rawPayment) {
        Payment paymentEntity = Payment.builder()
                .uuid(rawPayment.getUuid())
                .senderKey(rawPayment.getSenderKey())
                .receiverKey(rawPayment.getReceiverKey())
                .originalAmount(rawPayment.getAmount())
                .status(PaymentStatus.CREATED)
                .createdAt(rawPayment.getCreatedAt())
                .updateAt(rawPayment.getCreatedAt())
                .build();

        return paymentRepository.save(paymentEntity)
                .doOnSuccess(saved -> log.info("[PaymentService] Saved payment to MongoDB: {}", saved))
                .doOnError(e -> log.error("[PaymentService] Error persisting payment", e));
    }
}
