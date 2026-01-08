package com.example.transfer.port.outbound;

import com.example.transfer.dto.TransferRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ExternalTransferClient {
    void send(TransferRequest req)
            throws IOException, TimeoutException;
}

