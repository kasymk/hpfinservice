package com.example.transfer.service;

import com.example.transfer.domain.Account;
import com.example.transfer.dto.AccountDto;
import com.example.transfer.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Cacheable(
            cacheNames = "accountsByClient",
            key = "#clientId"
    )
    public List<AccountDto> getAccountsForClient(UUID clientId) {

        return accountRepository.findByClientId(clientId)
                .stream()
                .map(a -> new AccountDto(
                        a.getId(),
                        a.getCurrency(),
                        a.getStatus()
                ))
                .toList();
    }

    /**
     * IMPORTANT: cache invalidation
     */
    @CacheEvict(
            cacheNames = "accountsByClient",
            key = "#clientId"
    )
    public void evictClientAccounts(UUID clientId) {
        // intentionally empty
    }

    @Transactional
    @CacheEvict(
            cacheNames = "accountsByClient",
            key = "#clientId"
    )
    public UUID createAccount(UUID clientId, String currency) {

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setClientId(clientId);
        account.setCurrency(currency);
        account.setStatus("ACTIVE");

        accountRepository.save(account);

        return account.getId();
    }
}

