package org.pdzsoftware.payworld_direction_resolver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "defaultCurrency", url = "${feign.client.currency-api.main.url-prefix}@latest/v1/currencies")
public interface DefaultCurrencyClient {
    @GetMapping("/{currencyCode}.json")
    Map<String, Object> getRatesByCurrencyCode(@PathVariable String currencyCode);
}
