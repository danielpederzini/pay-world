package org.pdzsoftware.payworld_analytics_api.service;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentCountDto;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentDetailsDto;
import org.pdzsoftware.payworld_analytics_api.entity.MongoPayment;
import org.pdzsoftware.payworld_analytics_api.exception.custom.NotFoundException;
import org.pdzsoftware.payworld_analytics_api.repository.mongo.MongoPaymentRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentAnalyticsService {
    private final MongoPaymentRepository mongoPaymentRepository;

    public List<PaymentCountDto> getPaymentCount() {
        return mongoPaymentRepository.getPaymentCountByStatus();
    }

    public PaymentDetailsDto getPaymentDetails(String uuid) {
        MongoPayment mongoPayment = mongoPaymentRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("Payment not found for uuid: " + uuid));

        return PaymentDetailsDto.builder()
                .uuid(mongoPayment.getUuid())
                .senderKey(mongoPayment.getSenderKey())
                .receiverKey(mongoPayment.getReceiverKey())
                .originalCurrency(mongoPayment.getOriginalCurrency())
                .newCurrency(mongoPayment.getNewCurrency())
                .originalAmount(mongoPayment.getOriginalAmount())
                .convertedAmount(mongoPayment.getConvertedAmount())
                .status(mongoPayment.getStatus())
                .failureReason(mongoPayment.getFailureReason())
                .createdAt(mongoPayment.getCreatedAt())
                .convertedAt(mongoPayment.getConvertedAt())
                .processedAt(mongoPayment.getProcessedAt())
                .updatedAt(mongoPayment.getUpdatedAt())
                .totalDuration(Duration.between(
                        mongoPayment.getCreatedAt(), mongoPayment.getUpdatedAt()
                ))
                .build();
    }
}
