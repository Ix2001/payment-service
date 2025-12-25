package com.iprody.payment.service.app.exception;

import java.time.Instant;
import java.util.UUID;

public class ErrorDto {
    private final UUID id;
    private final String operation;
    private final String errorMessage;
    private final Instant timestamp;

    public ErrorDto(String errorMessage, String operation, UUID id) {
        this.errorMessage = errorMessage;
        this.operation = operation;
        this.id = id;
        this.timestamp = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getOperation() {
        return operation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
