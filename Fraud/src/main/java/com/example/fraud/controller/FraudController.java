package com.example.fraud.controller;

import com.example.fraud.dto.FraudCheckRequest;
import com.example.fraud.dto.FraudCheckResponse;
import com.example.fraud.model.FraudDecision;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fraud")
@Tag(name = "Fraud Detection", description = "Fraud decision API")
public class FraudController {

    @PostMapping("/check")
    @Operation(
            summary = "Check transfer for fraud",
            description = "Performs fraud detection for a money transfer. Currently always APPROVES.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Fraud decision returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FraudCheckResponse.class)
                            )
                    )
            }
    )
    public FraudCheckResponse check(
            @RequestBody FraudCheckRequest request) {

        return new FraudCheckResponse(
                FraudDecision.APPROVE,
                "Automatically approved"
        );
    }
}
