package org.pdzsoftware.payworld_direction_resolver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pdzsoftware.payworld_direction_resolver.client.AccountClient;
import org.pdzsoftware.payworld_direction_resolver.dto.AccountInfoDTO;
import org.pdzsoftware.payworld_direction_resolver.dto.EnrichedPaymentDTO;
import org.pdzsoftware.payworld_direction_resolver.dto.RawPaymentDTO;
import org.pdzsoftware.payworld_direction_resolver.publisher.EventPublisher;
import org.pdzsoftware.payworld_direction_resolver.repository.PaymentRepository;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.pdzsoftware.payworld_direction_resolver.entity.PaymentStatus.*;

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
        markMongoPaymentAsEnriched(enrichedPayment);
        trySendingPaymentToKafka(enrichedPayment);
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
        log.error("[PaymentService] Error while enriching payment with uuid: {}, reason: {}",
                rawPayment.getUuid(), e.getMessage());

        paymentRepository.markPaymentAsFailed(
                rawPayment.getUuid(), FAILED_AT_ENRICHMENT, e.getMessage(), LocalDateTime.now()
        );

        log.info("[PaymentService] Marked failed payment with uuid: {} as FAILED_AT_ENRICHMENT in MongoDB",
                rawPayment.getUuid());
    }

    private void markMongoPaymentAsEnriched(EnrichedPaymentDTO enrichedPayment) {
        paymentRepository.markPaymentAsEnriched(
                enrichedPayment.getUuid(), ENRICHED,
                enrichedPayment.getSenderKey(),
                enrichedPayment.getReceiverKey(),
                enrichedPayment.getOriginalCurrency(),
                enrichedPayment.getNewCurrency(),
                enrichedPayment.getOriginalAmount(),
                enrichedPayment.getConvertedAmount(),
                enrichedPayment.getConvertedAt()
        );

        log.info("[PaymentService] Updated enriched payment with uuid: {} as ENRICHED in MongoDB",
                enrichedPayment.getUuid());
    }

    private void trySendingPaymentToKafka(EnrichedPaymentDTO enrichedPayment) {
        CompletableFuture<SendResult<String, EnrichedPaymentDTO>> future = eventPublisher
                .publish(enrichedPayment.getUuid(), enrichedPayment);

        future.thenAccept(result -> {
            log.info("[EventPublisher] Sent message with key: {}", enrichedPayment.getUuid());
        }).exceptionally(ex -> {
            handleSendFailure(enrichedPayment, ex);
            throw new RuntimeException(ex);
        });
    }

    private void handleSendFailure(EnrichedPaymentDTO enrichedPayment, Throwable e) {
        log.error("[EventPublisher] Error sending message with key: {}, reason: {}",
                enrichedPayment.getUuid(), e.getMessage());

        paymentRepository.markPaymentAsFailed(
                enrichedPayment.getUuid(), FAILED_AT_PUBLISHING, e.getMessage(), LocalDateTime.now()
        );

        log.info("[PaymentService] Marked failed payment with uuid: {} as FAILED_AT_PUBLISHING in MongoDB",
                enrichedPayment.getUuid());
    }
}
