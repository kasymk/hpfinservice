package com.example.transfer.exception;

public class TemporaryFailureException extends RuntimeException {
    public TemporaryFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

