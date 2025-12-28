package com.snackapp.kitchen.core.application.exception;


public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("Pedido não encontrado: " + orderId);
    }
}
