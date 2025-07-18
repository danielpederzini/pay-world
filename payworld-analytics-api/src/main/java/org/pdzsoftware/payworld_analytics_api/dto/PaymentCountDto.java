package org.pdzsoftware.payworld_analytics_api.dto;

import lombok.*;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPaymentStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCountDto {
    private MongoPaymentStatus status;
    private long count;
}
