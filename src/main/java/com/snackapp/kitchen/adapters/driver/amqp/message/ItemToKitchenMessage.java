package com.snackapp.kitchen.adapters.driver.amqp.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ItemToKitchenMessage(
        @NotBlank String name,
        @Min(1) int quantity,
        @NotNull List<@Valid AddOnToKitchenMessage> addOns
) { }