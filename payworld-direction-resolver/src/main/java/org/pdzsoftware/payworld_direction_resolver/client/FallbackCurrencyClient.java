package org.pdzsoftware.payworld_direction_resolver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "fallbackCurrency", url = "${feign.client.currency-api.fallback.url-prefix}")
public interface FallbackCurrencyClient {
    @GetMapping("/{currencyCode}.json")
    Map<String, BigDecimal> getRatesByCurrencyCode(@PathVariable String currencyCode);
}
