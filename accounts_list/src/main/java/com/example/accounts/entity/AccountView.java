package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(
        name = "account_view",
        indexes = {
                @Index(name = "idx_account_client", columnList = "clientId")
        }
)
public class AccountView {

    @Id
    private String accountId;

    @Column(nullable = false)
    private String clientId;

    private BigDecimal balance;

    @Column(
            nullable = false,
            length = 3,
            columnDefinition = "char(3)"
    )
    private String currency;
    private Instant lastUpdated;

    protected AccountView() {}

    public AccountView(String accountId, String clientId, String currency) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.lastUpdated = Instant.now();
    }

    public AccountView(String accountId, String clientId, String currency, BigDecimal balance) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.currency = currency;
        this.balance = balance;
        this.lastUpdated = Instant.now();
    }

    public void applyDelta(BigDecimal delta) {
        this.balance = this.balance.add(delta);
        this.lastUpdated = Instant.now();
    }
}

