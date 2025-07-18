package org.pdzsoftware.payworld_analytics_api.service;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_analytics_api.dto.AccountOverviewDto;
import org.pdzsoftware.payworld_analytics_api.repository.sql.SqlAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountAnalyticsService {
    private final SqlAccountRepository sqlAccountRepository;

    public List<AccountOverviewDto> getAccountOverviews() {
        return sqlAccountRepository.getAccountOverviews();
    }
}
