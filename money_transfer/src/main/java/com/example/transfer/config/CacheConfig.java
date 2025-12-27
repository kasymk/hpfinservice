package com.example.transfer.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("accountsByClient");
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(100_000)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
        );
        return manager;
    }

    @Bean
    public CacheManager redisCacheManager(
            RedisConnectionFactory factory) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5))
                        .disableCachingNullValues()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()
                                        )
                        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    @Primary
    public CacheManager compositeCacheManager(
            CacheManager caffeineCacheManager,
            CacheManager redisCacheManager) {

        CompositeCacheManager manager = new CompositeCacheManager();
        manager.setCacheManagers(
                List.of(caffeineCacheManager, redisCacheManager)
        );
        manager.setFallbackToNoOpCache(false);
        return manager;
    }
}

