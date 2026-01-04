package com.example.accounts.exception;

import com.networknt.schema.ValidationMessage;

import java.util.Set;
import java.util.stream.Collectors;

public class SchemaValidationException extends RuntimeException {

    public SchemaValidationException(Set<ValidationMessage> errors) {
        super("Schema validation failed: " +
                errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining(", ")));
    }
}

