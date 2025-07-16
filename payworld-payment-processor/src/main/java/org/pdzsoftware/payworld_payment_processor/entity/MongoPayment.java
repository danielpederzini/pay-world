package org.pdzsoftware.payworld_payment_processor.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class MongoPayment {
    @MongoId
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
    private LocalDateTime updatedAt;
    private LocalDateTime convertedAt;
    private LocalDateTime processedAt;

    @Override
    public String toString() {
        return "MongoPayment{" +
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
                ", processedAt=" + processedAt +
                '}';
    }
}
