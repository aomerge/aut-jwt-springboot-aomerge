package com.aut_jwt.aut_jwt.dto;

import java.time.Instant;

public record ApiResponseDto(
        String error,
        int status,
        Object message,
        Instant timestamp,
        String path,
        String traceId
) {
    public ApiResponseDto(String error, int status, Object message, Instant timestamp, String path) {
        this(error, status, message, timestamp, path, null);
    }
}
