package com.snackapp.kitchen.adapters.driver.api.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ValidationErrorDetail> details
) {
    public record ValidationErrorDetail(String field, String message) {}
}
