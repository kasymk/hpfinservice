package com.example.transfer;

import com.example.transfer.dto.AccountDto;
import com.example.transfer.repository.AccountRepository;
import com.example.transfer.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceCacheTest {

    @Autowired
    private AccountService accountService;

    @SpyBean
    private AccountRepository accountRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCreateAccountAndUseCacheForFetch() {

        UUID clientId = UUID.randomUUID();

        // 1️⃣ Create first account
        accountService.createAccount(clientId, "USD");

        // 2️⃣ First fetch → DB hit
        List<AccountDto> firstCall =
                accountService.getAccountsForClient(clientId);

        assertEquals(1, firstCall.size());

        // 3️⃣ Second fetch → should be cached
        List<AccountDto> secondCall =
                accountService.getAccountsForClient(clientId);

        assertEquals(1, secondCall.size());

        // DB should be hit ONLY ONCE
        verify(accountRepository, times(1))
                .findByClientId(clientId);

        // 4️⃣ Create another account → cache eviction
        accountService.createAccount(clientId, "EUR");

        // 5️⃣ Fetch again → DB hit again (cache was evicted)
        List<AccountDto> thirdCall =
                accountService.getAccountsForClient(clientId);

        assertEquals(2, thirdCall.size());

        verify(accountRepository, times(2))
                .findByClientId(clientId);

        var cache = cacheManager.getCache("accountsByClient");
        assert cache != null;
        assert cache.get(clientId) != null;

    }
}
