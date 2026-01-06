package com.snackapp.kitchen.core.application.command;


import java.util.List;

public record ReceiveOrderCommand(
        Long orderId,
        List<Item> itens
) {
    public record Item(
            String name,
            int quantity,
            List<AddOn> addOns
    ) {}

    public record AddOn(
            String name,
            int quantity
    ) {}
}