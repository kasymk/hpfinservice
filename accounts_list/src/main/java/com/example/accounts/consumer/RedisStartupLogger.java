package com.example.accounts.consumer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisStartupLogger {

    @Value("${spring.redis.host:NOT_SET}")
    private String redisHost;

    @Value("${spring.redis.port:NOT_SET}")
    private String redisPort;

    @Value("${spring.redis.url:NOT_SET}")
    String redisUrl;

    @PostConstruct
    void logRedisConfig() {
        log.info(">>> Redis host = {}", redisHost);
        log.info(">>> Redis port = {}", redisPort);
        log.info(">>> Redis url  = {}", redisUrl);
    }
}

