package com.example.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "Account creation response")
public class CreateAccountResponse {

    @Schema(
            description = "Created account identifier",
            example = "8e1f0c77-5a45-4a66-b1a4-8c3c6d94b321"
    )
    private UUID accountId;
}

