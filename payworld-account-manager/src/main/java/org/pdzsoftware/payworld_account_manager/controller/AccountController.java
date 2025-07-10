package org.pdzsoftware.payworld_account_manager.controller;

import lombok.RequiredArgsConstructor;
import org.pdzsoftware.payworld_account_manager.dto.AccountInfoDTO;
import org.pdzsoftware.payworld_account_manager.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/keys")
    public ResponseEntity<List<String>> getAccountKeys() {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountKeys());
    }

    @GetMapping("/{key}")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@PathVariable String key) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountInfo(key));
    }
}
