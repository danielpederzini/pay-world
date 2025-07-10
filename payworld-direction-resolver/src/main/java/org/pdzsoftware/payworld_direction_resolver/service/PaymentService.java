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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountClient accountClient;
    private final CurrencyRatesService currencyRatesService;
    private final EventPublisher eventPublisher;

    public void processPayment(RawPaymentDTO payment) {
        EnrichedPaymentDTO enrichedPayment = getEnrichedPayment(payment);
        eventPublisher.publish(enrichedPayment.getUuid(), enrichedPayment);
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
                .status(PaymentStatus.PENDING)
                .build();
    }

    private EnrichedPaymentDTO getEnrichedPayment(RawPaymentDTO payment) {
        AccountInfoDTO senderInfo = accountClient.getInfoByKey(payment.getSenderKey());
        AccountInfoDTO receiverInfo = accountClient.getInfoByKey(payment.getReceiverKey());

        BigDecimal conversionRate = currencyRatesService.getRateByCurrencyCodes(
                senderInfo.getCurrency(), receiverInfo.getCurrency()
        );

        BigDecimal convertedAmount = payment.getAmount().multiply(conversionRate);

        return EnrichedPaymentDTO.builder()
                .uuid(payment.getUuid())
                .senderKey(senderInfo.getKey())
                .originalCurrency(senderInfo.getCurrency())
                .originalAmount(payment.getAmount())
                .receiverKey(receiverInfo.getKey())
                .newCurrency(receiverInfo.getCurrency())
                .convertedAmount(convertedAmount)
                .createdAt(payment.getCreatedAt())
                .convertedAt(LocalDateTime.now())
                .build();
    }
}
