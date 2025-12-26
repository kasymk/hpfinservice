package com.example.transfer.controller;

import com.example.transfer.dto.AccountDto;
import com.example.transfer.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{clientId}/accounts")
    public List<AccountDto> getClientAccounts(
            @PathVariable UUID clientId) {

        return accountService.getAccountsForClient(clientId);
    }
}

