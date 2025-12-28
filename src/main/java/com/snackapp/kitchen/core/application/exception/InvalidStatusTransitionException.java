package com.snackapp.kitchen.core.application.exception;


import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(KitchenOrderStatus current, KitchenOrderStatus next) {
        super("Transição inválida: " + current + " -> " + next);
    }
}