package org.pdzsoftware.payworld_payment_processor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;
    private String key;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "senderAccount")
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiverAccount")
    private List<Transaction> receivedTransactions;
}
