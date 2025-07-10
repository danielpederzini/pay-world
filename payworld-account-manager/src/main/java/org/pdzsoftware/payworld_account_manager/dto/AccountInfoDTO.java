package org.pdzsoftware.payworld_account_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private String uuid;
    private String key;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
}
