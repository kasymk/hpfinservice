package com.example.transfer;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.exception.TemporaryFailureException;
import com.example.transfer.port.outbound.ExternalTransferClient;
import com.example.transfer.service.TransferPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RetryIntegrationTest {

    @Autowired
    private TransferPostProcessor postProcessor;

    @MockBean
    private ExternalTransferClient externalClient;

    @Test
    void shouldRetryOnTemporaryFailureAndEventuallySucceed() throws Exception {

        TransferRequest req = new TransferRequest();
        req.setFromAccount(UUID.randomUUID());
        req.setToAccount(UUID.randomUUID());
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency("USD");

        doThrow(new TemporaryFailureException("fail 1", null))
                .doThrow(new TemporaryFailureException("fail 2", null))
                .doNothing()
                .when(externalClient)
                .send(any());

        assertDoesNotThrow(() ->
                postProcessor.handlePostTransfer(req)
        );

        verify(externalClient, times(3))
                .send(any());
    }
}


