package org.pdzsoftware.payworld_analytics_api.service;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentCountDto;
import org.pdzsoftware.payworld_analytics_api.repository.mongo.MongoPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final MongoPaymentRepository mongoPaymentRepository;

    public List<PaymentCountDto> getPaymentCount() {
        return mongoPaymentRepository.getPaymentCountByStatus();
    }
}
