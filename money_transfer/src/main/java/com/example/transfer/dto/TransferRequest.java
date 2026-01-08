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
@Schema(
        name = "TransferRequest",
        description = "Request payload for initiating a money transfer"
)
public class TransferRequest {

    @NotNull
    @Schema(
            description = "Source account ID",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            format = "uuid"
    )
    private UUID fromAccount;

    @NotNull
    @Schema(
            description = "Destination account ID",
            example = "6b3a3f77-3dbe-4f88-9c16-0a0f9c8c6e91",
            format = "uuid"
    )
    private UUID toAccount;

    @NotNull
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero"
    )
    @Schema(
            description = "Transfer amount",
            example = "150.75",
            minimum = "0.01"
    )
    private BigDecimal amount;

    @NotBlank
    @Schema(
            description = "ISO 4217 currency code",
            example = "USD",
            minLength = 3,
            maxLength = 3
    )
    private String currency;
}
