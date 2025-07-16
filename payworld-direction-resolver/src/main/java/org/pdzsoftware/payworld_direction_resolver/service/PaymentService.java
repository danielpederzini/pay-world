package org.pdzsoftware.payworld_direction_resolver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_direction_resolver.client.AccountClient;
import org.pdzsoftware.payworld_direction_resolver.dto.AccountInfoDTO;
import org.pdzsoftware.payworld_direction_resolver.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_direction_resolver.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_direction_resolver.entity.Payment;
import org.pdzsoftware.payworld_direction_resolver.entity.PaymentStatus;
import org.pdzsoftware.payworld_direction_resolver.publisher.EventPublisher;
import org.pdzsoftware.payworld_direction_resolver.repository.PaymentRepository;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountClient accountClient;
    private final CurrencyRatesService currencyRatesService;

    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;

    public void processPayment(RawPaymentDTO rawPayment) {
        EnrichedPaymentDTO enrichedPayment = enrichPayment(rawPayment);
        Payment paymentEntity = buildPaymentEntity(enrichedPayment);

        CompletableFuture<SendResult<String, EnrichedPaymentDTO>> future = eventPublisher
                .publish(enrichedPayment.getUuid(), enrichedPayment);

        future.thenAccept(result -> handleSendSuccess(paymentEntity))
                .exceptionally(ex -> {
                    handleSendFailure(ex, paymentEntity);
                    throw new RuntimeException(ex);
                });
    }

    private EnrichedPaymentDTO enrichPayment(RawPaymentDTO rawPayment) {
        try {
            AccountInfoDTO senderInfo = accountClient.getInfoByKey(rawPayment.getSenderKey());
            AccountInfoDTO receiverInfo = accountClient.getInfoByKey(rawPayment.getReceiverKey());

            BigDecimal conversionRate = currencyRatesService.getRateByCurrencyCodes(
                    senderInfo.getCurrency(), receiverInfo.getCurrency()
            );

            BigDecimal convertedAmount = rawPayment.getAmount().multiply(conversionRate);

            return EnrichedPaymentDTO.builder()
                    .uuid(rawPayment.getUuid())
                    .senderKey(senderInfo.getKey())
                    .originalCurrency(senderInfo.getCurrency())
                    .originalAmount(rawPayment.getAmount())
                    .receiverKey(receiverInfo.getKey())
                    .newCurrency(receiverInfo.getCurrency())
                    .convertedAmount(convertedAmount)
                    .createdAt(rawPayment.getCreatedAt())
                    .convertedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            handleEnrichmentFailure(rawPayment, e);
            throw e;
        }
    }

    private void handleEnrichmentFailure(RawPaymentDTO rawPayment, Exception e) {
        log.error("[PaymentService] Error while enriching payment with key: {}, reason: {}",
                rawPayment.getUuid(), e.getMessage());

        Payment paymentEntity = buildPaymentEntity(rawPayment);
        paymentEntity.setStatus(PaymentStatus.FAILED_AT_ENRICHMENT);
        paymentEntity.setFailureReason(e.getMessage());
        paymentRepository.save(paymentEntity);
    }

    private void handleSendFailure(Throwable ex, Payment paymentEntity) {
        log.error("[EventPublisher] Error sending message with key: {}", paymentEntity.getUuid(), ex);

        paymentEntity.setStatus(PaymentStatus.FAILED_AT_PUBLISHING);
        paymentEntity.setFailureReason(ex.getMessage());
        paymentRepository.save(paymentEntity);
    }

    private void handleSendSuccess(Payment paymentEntity) {
        log.info("[EventPublisher] Sent message with key: {}", paymentEntity.getUuid());

        paymentEntity.setStatus(PaymentStatus.ENRICHED);
        paymentRepository.save(paymentEntity);
    }

    private static Payment buildPaymentEntity(EnrichedPaymentDTO enrichedPayment) {
        return Payment.builder()
                .uuid(enrichedPayment.getUuid())
                .senderKey(enrichedPayment.getSenderKey())
                .receiverKey(enrichedPayment.getReceiverKey())
                .originalCurrency(enrichedPayment.getOriginalCurrency())
                .newCurrency(enrichedPayment.getNewCurrency())
                .originalAmount(enrichedPayment.getOriginalAmount())
                .convertedAmount(enrichedPayment.getConvertedAmount())
                .createdAt(enrichedPayment.getCreatedAt())
                .updatedAt(enrichedPayment.getConvertedAt())
                .convertedAt(enrichedPayment.getConvertedAt())
                .build();
    }

    private static Payment buildPaymentEntity(RawPaymentDTO rawPaymentDTO) {
        return Payment.builder()
                .uuid(rawPaymentDTO.getUuid())
                .senderKey(rawPaymentDTO.getSenderKey())
                .receiverKey(rawPaymentDTO.getReceiverKey())
                .createdAt(rawPaymentDTO.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
