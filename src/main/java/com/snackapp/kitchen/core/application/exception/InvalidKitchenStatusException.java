package com.snackapp.kitchen.core.application.exception;


public class InvalidKitchenStatusException extends RuntimeException {
    public InvalidKitchenStatusException(String value) {
        super("Status inválido: " + value);
    }
}
