package org.pdzsoftware.payworld_direction_resolver.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrichedPaymentDTO {
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private Currency originalCurrency;
    private Currency newCurrency;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private LocalDateTime createdAt;
    private LocalDateTime convertedAt;

    @Override
    public String toString() {
        return "EnrichedPaymentDTO{" +
                "uuid='" + uuid + '\'' +
                ", senderKey='" + senderKey + '\'' +
                ", receiverKey='" + receiverKey + '\'' +
                ", originalCurrency=" + originalCurrency +
                ", newCurrency=" + newCurrency +
                ", originalAmount=" + originalAmount +
                ", convertedAmount=" + convertedAmount +
                ", createdAt=" + createdAt +
                ", convertedAt=" + convertedAt +
                '}';
    }
}
