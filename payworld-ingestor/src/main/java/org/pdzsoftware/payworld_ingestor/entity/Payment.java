package org.pdzsoftware.payworld_ingestor.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @Override
    public String toString() {
        return "Payment{" +
                "uuid='" + uuid + '\'' +
                ", senderKey='" + senderKey + '\'' +
                ", receiverKey='" + receiverKey + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
