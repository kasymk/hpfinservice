package com.example.transfer.controller;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
@Tag(
        name = "Transfers",
        description = "Money transfer operations"
)
public class TransferController {

    private final TransferService service;

    @PostMapping
    @Operation(
            summary = "Create money transfer",
            description = "Initiates a money transfer.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Transfer accepted for processing"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Duplicate Idempotency-Key",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Void> transfer(
            @Parameter(
                    name = "Idempotency-Key",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Ensures idempotent transfer creation",
                    schema = @Schema(format = "uuid")
            )
            @RequestHeader("Idempotency-Key") UUID key,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Transfer request payload",
                    content = @Content(
                            schema = @Schema(implementation = TransferRequest.class)
                    )
            )
            @Valid @RequestBody TransferRequest req) {

        service.transfer(key, req);
        return ResponseEntity.accepted().build();
    }
}


