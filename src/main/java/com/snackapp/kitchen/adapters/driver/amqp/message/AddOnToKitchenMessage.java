package com.snackapp.kitchen.adapters.driver.amqp.message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddOnToKitchenMessage(
        @NotBlank String name,
        @Min(1) int quantity
) { }