package org.pdzsoftware.payworld_analytics_api.controller;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.dto.AccountOverviewDto;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentCountDto;
import org.pdzsoftware.payworld_analytics_api.dto.PaymentDetailsDto;
import org.pdzsoftware.payworld_analytics_api.service.AccountAnalyticsService;
import org.pdzsoftware.payworld_analytics_api.service.PaymentAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnalyticsController {
    private final PaymentAnalyticsService paymentAnalyticsService;
    private final AccountAnalyticsService accountAnalyticsService;

    @GetMapping("/payments/count")
    public ResponseEntity<List<PaymentCountDto>> getPaymentCount() {
        return ResponseEntity.status(HttpStatus.OK).body(paymentAnalyticsService.getPaymentCount());
    }

    @GetMapping("/payments/{uuid}")
    public ResponseEntity<PaymentDetailsDto> getPaymentDetails(@PathVariable String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentAnalyticsService.getPaymentDetails(uuid));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountOverviewDto>> getAccountOverviews() {
        return ResponseEntity.status(HttpStatus.OK).body(accountAnalyticsService.getAccountOverviews());
    }
}
