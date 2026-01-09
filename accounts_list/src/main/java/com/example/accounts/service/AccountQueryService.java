package com.example.accounts.service;

import com.example.accounts.entity.AccountView;
import com.example.accounts.repository.AccountViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountQueryService {

    private final AccountViewRepository repository;

    public AccountQueryService(AccountViewRepository repository) {
        this.repository = repository;
    }

    @Cacheable(
            cacheNames = "accounts",
            key = "'client:' + #clientId"
    )
    public List<AccountView> getAccountsByClient(String clientId) {
        log.info("Getting list of accounts for client: {}", clientId);
        return repository.findByClientId(clientId);
    }

    @CacheEvict(
            cacheNames = "accounts",
            key = "'client:' + #clientId"
    )
    public void evictClientAccounts(String clientId) {
        // eviction only
    }
}

