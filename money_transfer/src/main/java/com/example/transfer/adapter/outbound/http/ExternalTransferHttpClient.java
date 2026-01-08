package com.example.transfer.adapter.outbound.http;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.port.outbound.ExternalTransferClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class ExternalTransferHttpClient
        implements ExternalTransferClient {

    @Override
    public void send(TransferRequest req)
            throws IOException, TimeoutException {
        // HTTP call
    }
}
