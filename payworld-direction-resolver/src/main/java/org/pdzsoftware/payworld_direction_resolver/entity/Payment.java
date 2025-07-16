package org.pdzsoftware.payworld_direction_resolver.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @MongoId
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private Currency originalCurrency;
    private Currency newCurrency;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private PaymentStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime convertedAt;

    @Override
    public String toString() {
        return "Payment{" +
                "uuid='" + uuid + '\'' +
                ", senderKey='" + senderKey + '\'' +
                ", receiverKey='" + receiverKey + '\'' +
                ", originalCurrency=" + originalCurrency +
                ", newCurrency=" + newCurrency +
                ", originalAmount=" + originalAmount +
                ", convertedAmount=" + convertedAmount +
                ", status=" + status +
                ", failureReason='" + failureReason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", convertedAt=" + convertedAt +
                '}';
    }
}
