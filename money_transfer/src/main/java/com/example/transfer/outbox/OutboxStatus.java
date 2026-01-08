package com.example.transfer.outbox;

public enum OutboxStatus {

    NEW("NEW"),
    SENT("SENT"),
    FAILED("FAILED");

    private final String value;

    OutboxStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

