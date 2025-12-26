package com.example.transfer.dto;

import java.util.UUID;

public record AccountDto(
        UUID id,
        String currency,
        String status
) {}

