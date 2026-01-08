package com.example.transfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Request to create a new account")
public class CreateAccountRequest {

    @NotNull
    @Schema(
            description = "Client identifier",
            example = "c1b9c1f4-7a2c-4e1c-9e93-8f8e8a1f0a11"
    )
    private UUID clientId;

    @NotBlank
    @Schema(
            description = "Account currency (ISO 4217)",
            example = "USD"
    )
    private String currency;

    @NotNull
    @DecimalMin("0.00")
    @Schema(
            description = "Initial account balance",
            example = "1000.00",
            minimum = "0"
    )
    private BigDecimal initialBalance;
}

