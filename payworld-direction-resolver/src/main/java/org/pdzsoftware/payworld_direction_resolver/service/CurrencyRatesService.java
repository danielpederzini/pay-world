package org.pdzsoftware.payworld_direction_resolver.service;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_direction_resolver.client.DefaultCurrencyClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyRatesService {
    private final DefaultCurrencyClient defaultCurrencyClient;

    @Cacheable(value = "conversionRates", key = "{#originalCurrency,#newCurrency}")
    public BigDecimal getRateByCurrencyCodes(Currency originalCurrency, Currency newCurrency) {
        String originalCurrencyCode = originalCurrency.getCurrencyCode().toLowerCase();
        String newCurrencyCode = newCurrency.getCurrencyCode().toLowerCase();

        Map<String, Object> json = defaultCurrencyClient.getRatesByCurrencyCode(originalCurrencyCode);
        Map<String, Double> rates = (Map<String, Double>) json.get(originalCurrencyCode);

        return BigDecimal.valueOf(rates.get(newCurrencyCode));
    }
}
