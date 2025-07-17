package org.pdzsoftware.payworld_payment_processor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    private String uuid;
    private BigDecimal originalAmount;
    private Currency originalCurrency;
    private BigDecimal convertedAmount;
    private Currency newCurrency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_fk")
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_fk")
    private Account receiverAccount;

    @Override
    public String toString() {
        return "Transaction{" +
                "uuid='" + uuid + '\'' +
                ", originalAmount=" + originalAmount +
                ", originalCurrency=" + originalCurrency +
                ", convertedAmount=" + convertedAmount +
                ", newCurrency=" + newCurrency +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", senderAccount=" + senderAccount +
                ", receiverAccount=" + receiverAccount +
                '}';
    }
}
