package org.pdzsoftware.payworld_ingestor.dto;

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
}
