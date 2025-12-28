package com.snackapp.kitchen.adapters.driver.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateKitchenStatusRequest(
        @NotBlank
        String status
) {}