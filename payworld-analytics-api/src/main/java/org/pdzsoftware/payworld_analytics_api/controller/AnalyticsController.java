package org.pdzsoftware.payworld_analytics_api.controller;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentCountDto;
import org.pdzsoftware.payworld_analytics_api.service.AnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/payments/count")
    public ResponseEntity<List<PaymentCountDto>> getPaymentCount() {
        return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getPaymentCount());
    }
}
