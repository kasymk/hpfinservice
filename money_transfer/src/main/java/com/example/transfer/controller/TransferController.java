package com.example.transfer.controller;

import com.example.transfer.dto.TransferRequest;
import com.example.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @PostMapping
    public ResponseEntity<Void> transfer(
            @RequestHeader("Idempotency-Key") UUID key,
            @Valid @RequestBody TransferRequest req) {

        service.transfer(key, req);
        return ResponseEntity.accepted().build();
    }
}

