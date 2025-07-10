package org.pdzsoftware.payworld_account_manager.service;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_account_manager.dto.AccountInfoDTO;
import org.pdzsoftware.payworld_account_manager.exception.custom.NotFoundException;
import org.pdzsoftware.payworld_account_manager.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public List<String> getAccountKeys() {
        return accountRepository.findAllKeys();
    }

    public AccountInfoDTO getAccountInfo(String key) {
        return accountRepository.findInfoByKey(key).orElseThrow(() ->
                new NotFoundException("Account not found for key: " + key));
    }
}
