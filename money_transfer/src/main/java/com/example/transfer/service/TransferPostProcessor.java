package com.example.transfer.service;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.exception.TemporaryFailureException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransferPostProcessor {
    private final AtomicInteger attempts = new AtomicInteger();

    @Retryable(
            retryFor = TemporaryFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 200,
                    multiplier = 2,
                    maxDelay = 5000
            )
    )
    public void handlePostTransfer(TransferRequest req) {
        notifyExternalSystem(req);
    }

    @Recover
    public void recover(
            TemporaryFailureException ex,
            TransferRequest req
    ) {
        // mark transfer as PENDING_RETRY
        // or enqueue for async retry
    }

    @CircuitBreaker(
            name = "externalService",
            fallbackMethod = "fallback"
    )
    public void notifyExternalSystem(TransferRequest req) {
        try {
            if (attempts.incrementAndGet() < 3) {
                throw new TemporaryFailureException("Temporary failure", null);
            }
            sendExternalRequest(req);
        } catch (IOException | TimeoutException ex) {
            throw new TemporaryFailureException(
                    "External service unavailable", ex
            );
        }
    }

    public void fallback(TransferRequest req, Throwable t) {
        // enqueue for async processing
    }

    private void sendExternalRequest(TransferRequest req) throws IOException, TimeoutException{
    }

    public int attempts() {
        return attempts.get();
    }
}

