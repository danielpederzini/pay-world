package org.pdzsoftware.payworld_analytics_api.dto;

import lombok.*;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPaymentStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDto {
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private Currency originalCurrency;
    private Currency newCurrency;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private MongoPaymentStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime convertedAt;
    private LocalDateTime processedAt;
    private LocalDateTime updatedAt;
    private Duration totalDuration;
}
