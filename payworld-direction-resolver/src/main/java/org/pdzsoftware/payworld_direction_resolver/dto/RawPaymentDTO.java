package org.pdzsoftware.payworld_direction_resolver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawPaymentDTO {
    private String uuid;
    private String senderKey;
    private String receiverKey;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "RawPaymentDTO{" +
                "uuid='" + uuid + '\'' +
                ", senderKey='" + senderKey + '\'' +
                ", receiverKey='" + receiverKey + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}
