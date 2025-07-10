package org.pdzsoftware.payworld_direction_resolver.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private Currency originalCurrency;
    private Currency newCurrency;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime convertedAt;
    private PaymentStatus status;
}
