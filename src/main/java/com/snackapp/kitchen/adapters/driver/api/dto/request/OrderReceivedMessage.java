package com.snackapp.kitchen.adapters.driver.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderReceivedMessage {

    @NotBlank
    private String orderId;

    @NotNull
    private List<Item> items;

    @Data
    public static class Item {
        @NotBlank
        private String productId;

        private String name;

        @NotNull
        private Integer quantity;
    }
}