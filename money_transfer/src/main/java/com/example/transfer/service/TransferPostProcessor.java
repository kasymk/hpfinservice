package com.example.transfer.service;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.exception.TemporaryFailureException;
import com.example.transfer.port.outbound.ExternalTransferClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferPostProcessor {

    private final ExternalTransferClient externalClient;

    @Retry(
            name = "externalServiceRetry",
            fallbackMethod = "recover"
    )
    @CircuitBreaker(
            name = "externalServiceCircuitBreaker"
    )
    public void handlePostTransfer(TransferRequest req) {
        try {
            log.info("Calling external system");
            externalClient.send(req);
        } catch (IOException | TimeoutException ex) {
            throw new TemporaryFailureException(
                    "External service unavailable", ex
            );
        }
    }

    public void recover(TransferRequest req, Throwable ex) {
        // mark transfer as PENDING_RETRY
        // enqueue for async retry / outbox
    }
}

