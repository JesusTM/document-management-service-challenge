package com.clara.ops.challenge.document_management_service_challenge.exception;

import java.time.Instant;

public record ApiError(
        int status,
        String error,
        String message,
        Instant timestamp
) {}
