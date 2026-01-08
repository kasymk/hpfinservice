package com.example.transfer.service;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.exception.BusinessException;
import com.example.transfer.fraud.FraudCheckRequest;
import com.example.transfer.fraud.FraudCheckService;
import com.example.transfer.fraud.FraudDecision;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferService {
    private final TransferExecutor transferExecutor;
    private final TransferPostProcessor postProcessor;
    private final FraudCheckService fraudCheckService;

    public TransferService(
            TransferExecutor transferExecutor,
            TransferPostProcessor postProcessor,
            FraudCheckService fraudCheckService
    ) {
        this.transferExecutor = transferExecutor;
        this.postProcessor = postProcessor;
        this.fraudCheckService = fraudCheckService;
    }

    public void transfer(UUID idempotencyKey, TransferRequest req) {
        UUID transferId = UUID.randomUUID();
//        fraudCheck(transferId, req);
        transferExecutor.performTransfer(idempotencyKey, req, transferId);

        postProcessor.handlePostTransfer(req);
    }

    private void fraudCheck(UUID transferId, TransferRequest req) {
        FraudDecision decision =
                fraudCheckService.check(
                        new FraudCheckRequest(
                                transferId,
                                req.getFromAccount(),
                                req.getToAccount(),
                                req.getAmount(),
                                req.getCurrency()
                        )
                );

        if (decision == FraudDecision.REJECT) {
            throw new BusinessException("Transfer rejected by fraud system");
        }

        if (decision == FraudDecision.REVIEW) {
            throw new BusinessException("Transfer pending fraud review");
        }
    }

}

