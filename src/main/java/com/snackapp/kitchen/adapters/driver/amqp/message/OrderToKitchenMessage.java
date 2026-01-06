package com.snackapp.kitchen.adapters.driver.amqp.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderToKitchenMessage(
        @NotNull Long orderId,
        @NotEmpty List<@Valid ItemToKitchenMessage> itens
) { }