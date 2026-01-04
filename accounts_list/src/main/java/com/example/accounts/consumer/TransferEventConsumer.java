package com.example.accounts.consumer;

import com.example.accounts.entity.AccountView;
import com.example.accounts.dto.TransferCompletedEvent;
import com.example.accounts.entity.ProcessedEvent;
import com.example.accounts.repository.AccountViewRepository;
import com.example.accounts.repository.ProcessedEventRepository;
import com.example.accounts.service.AccountQueryService;
import com.example.accounts.validation.TransferEventSchemaValidator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransferEventConsumer {

    private final AccountViewRepository accountRepo;
    private final ProcessedEventRepository processedRepo;
    private final TransferEventSchemaValidator validator;
    private final AccountQueryService cacheService;

    public TransferEventConsumer(
            AccountViewRepository accountRepo,
            ProcessedEventRepository processedRepo,
            TransferEventSchemaValidator validator, AccountQueryService cacheService) {
        this.accountRepo = accountRepo;
        this.processedRepo = processedRepo;
        this.validator = validator;
        this.cacheService = cacheService;
    }

    @KafkaListener(
            topics = "transfer.completed",
            groupId = "account-listing-service"
    )
    @Transactional
    public void onTransferCompleted(TransferCompletedEvent event) {
        log.info("CONSUMING event started {}", event.getEventId());
        validator.validate(event);

        if (processedRepo.existsById(event.getEventId())) {
            log.info("CONSUMING event aborted due to duplicate. Event id: {}", event.getEventId());
            return;
        }

        applyTransfer(event);

        processedRepo.save(new ProcessedEvent(event.getEventId()));

        cacheService.evictClientAccounts(event.getFromClientId());
        cacheService.evictClientAccounts(event.getToClientId());
        log.info("CONSUMING event finished {}", event.getEventId());
    }

    private void applyTransfer(TransferCompletedEvent event) {
        AccountView from = accountRepo
                .findById(event.getFromAccountId())
                .orElse(new AccountView(
                        event.getFromAccountId(),
                        event.getFromClientId(),
                        event.getCurrency()
                ));

        from.applyDelta(event.getAmount().negate());
        accountRepo.save(from);

        AccountView to = accountRepo
                .findById(event.getToAccountId())
                .orElse(new AccountView(
                        event.getToAccountId(),
                        event.getToClientId(),
                        event.getCurrency()
                ));

        to.applyDelta(event.getAmount());
        accountRepo.save(to);
    }
}
