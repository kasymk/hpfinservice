package com.example.transfer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {

    @Id
    private UUID id;

    @Column(
            nullable = false,
            length = 3,
            columnDefinition = "char(3)"
    )
    private String currency;

    @Column(nullable = false)
    private String status;
}

