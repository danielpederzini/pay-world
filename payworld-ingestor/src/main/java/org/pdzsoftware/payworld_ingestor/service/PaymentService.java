package org.pdzsoftware.payworld_ingestor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_ingestor.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_ingestor.entity.Payment;
import org.pdzsoftware.payworld_ingestor.entity.PaymentStatus;
import org.pdzsoftware.payworld_ingestor.publisher.EventPublisher;
import org.pdzsoftware.payworld_ingestor.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.pdzsoftware.payworld_ingestor.entity.PaymentStatus.FAILED_AT_PUBLISHING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;

    public Mono<Void> process(RawPaymentDTO body) {
        return paymentRepository.save(buildPaymentEntity(body))
                .doOnSuccess(p -> handleSaveSuccess(body.getUuid()))
                .onErrorResume(e -> handleSaveError(body.getUuid(), e))
                .flatMap(p -> eventPublisher.publish(body.getUuid(), body)
                        .doOnSuccess(v -> handlePublishSuccess(body.getUuid()))
                        .onErrorResume(e -> handlePublishError(body, e))
                );
    }

    private void handleSaveSuccess(String uuid) {
        log.info("[PaymentService] Saved payment with uuid: {} as CREATED in MongoDB", uuid);
    }

    private Mono<Payment> handleSaveError(String uuid, Throwable e) {
        log.error("[PaymentService] Error saving payment with uuid: {}, reason: {}", uuid, e.getMessage(), e);
        return Mono.empty();
    }

    private void handlePublishSuccess(String uuid) {
        log.info("[EventPublisher] Sent message with key: {}", uuid);
    }

    private Mono<Void> handlePublishError(RawPaymentDTO body, Throwable e) {
        log.error("[EventPublisher] Error sending message with key: {}, reason: {}",
                body.getUuid(), e.getMessage(), e);

        return paymentRepository.markPaymentAsFailed(
                        body.getUuid(),
                        FAILED_AT_PUBLISHING,
                        e.getMessage(),
                        LocalDateTime.now()
                ).doOnSuccess(v -> handleUpdateSuccess(body.getUuid()));
    }

    private void handleUpdateSuccess(String uuid) {
        log.info("[PaymentService] Marked payment with uuid: {} as FAILED_AT_PUBLISHING in MongoDB", uuid);
    }

    private static Payment buildPaymentEntity(RawPaymentDTO body) {
        return Payment.builder()
                .uuid(body.getUuid())
                .senderKey(body.getSenderKey())
                .receiverKey(body.getReceiverKey())
                .originalAmount(body.getAmount())
                .status(PaymentStatus.CREATED)
                .createdAt(body.getCreatedAt())
                .updatedAt(body.getCreatedAt())
                .build();
    }
}
