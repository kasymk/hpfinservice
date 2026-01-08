package com.example.transfer.controller;

import com.example.transfer.dto.CreateAccountRequest;
import com.example.transfer.dto.CreateAccountResponse;
import com.example.transfer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management operations")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(
            summary = "Create a new account",
            description = "Creates a new account for a client with an initial balance. For testing purposes!!!",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Account successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateAccountResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<CreateAccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {

        UUID accountId = accountService.createAccount(
                request.getClientId(),
                request.getCurrency(),
                request.getInitialBalance()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateAccountResponse(accountId));
    }
}
