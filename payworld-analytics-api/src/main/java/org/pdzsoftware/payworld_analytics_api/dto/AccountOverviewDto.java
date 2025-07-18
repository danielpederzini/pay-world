package org.pdzsoftware.payworld_analytics_api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

@Getter
@Setter
public class AccountOverviewDto {
    private String uuid;
    private String key;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
    private Long totalPaymentsSent;
    private Long totalPaymentsReceived;
    private BigDecimal averageDebited;
    private BigDecimal averageCredited;
    private BigDecimal totalDebited;
    private BigDecimal totalCredited;
    private BigDecimal operationTotal;

    public AccountOverviewDto(String uuid,
                              String key,
                              BigDecimal balance,
                              Currency currency,
                              LocalDateTime createdAt,
                              long totalPaymentsSent,
                              long totalPaymentsReceived,
                              double averageDebited,
                              double averageCredited,
                              BigDecimal totalDebited,
                              BigDecimal totalCredited) {
        this.uuid = uuid;
        this.key = key;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
        this.totalPaymentsSent = totalPaymentsSent;
        this.totalPaymentsReceived = totalPaymentsReceived;
        this.averageDebited = BigDecimal.valueOf(averageDebited);
        this.averageCredited = BigDecimal.valueOf(averageCredited);
        this.totalDebited = totalDebited;
        this.totalCredited = totalCredited;
        this.operationTotal = totalCredited.add(totalDebited.negate());
    }
}
