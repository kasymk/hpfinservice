package com.example.accounts.controller;

import com.example.accounts.entity.AccountView;
import com.example.accounts.repository.AccountViewRepository;
import com.example.accounts.service.AccountQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/accounts")
public class AccountController {

    private final AccountViewRepository repository;
    private final AccountQueryService queryService;

    public AccountController(AccountViewRepository repository, AccountQueryService queryService) {
        this.repository = repository;
        this.queryService = queryService;
    }

    @GetMapping
    public List<AccountView> listClientAccounts(
            @PathVariable String clientId) {

        return queryService.getAccountsByClient(clientId);
    }

    @GetMapping("/{accountId}")
    public AccountView getAccount(
            @PathVariable String clientId,
            @PathVariable String accountId) {

        AccountView account = repository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND));

        if (!account.getClientId().equals(clientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return account;
    }
}
