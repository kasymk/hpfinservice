package com.example.transfer;

import com.example.transfer.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class RedisCacheIntegrationTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port",
                () -> redis.getMappedPort(6379));
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldStoreAccountsInRedisCache() {

        UUID clientId = UUID.randomUUID();

        // create account (this also evicts cache safely)
        accountService.createAccount(clientId, "USD");

        // first call → DB → cache
        accountService.getAccountsForClient(clientId);

        // verify cache entry exists (L2 Redis)
        var cache = cacheManager.getCache("accountsByClient");
        assertNotNull(cache);

        var value = cache.get(clientId);
        assertNotNull(value, "Expected value to be present in Redis cache");
    }
}
