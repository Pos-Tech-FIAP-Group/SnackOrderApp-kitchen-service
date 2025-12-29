package com.snackapp.kitchen.core.domain.model;

import com.snackapp.kitchen.core.application.exception.InvalidStatusTransitionException;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class KitchenOrderTest {

    @Test
    @DisplayName("Deve salvar pedido recebido como RECEBIDO de forma idempotente")
    void shouldAllowReceivedToInPreparation() {
        KitchenOrder order = KitchenOrder.builder()
                .orderId("1")
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.parse("2025-12-28T00:00:00Z"))
                .build();

        KitchenOrder updated = order.changeStatusTo(KitchenOrderStatus.EM_PREPARACAO);

        assertEquals("1", updated.getOrderId());
        assertEquals(KitchenOrderStatus.EM_PREPARACAO, updated.getStatus());
        assertEquals(order.getCreatedAt(), updated.getCreatedAt());
    }

    @Test
    void shouldRejectInvalidTransition() {
        KitchenOrder order = KitchenOrder.builder()
                .orderId("1")
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .build();

        assertThrows(InvalidStatusTransitionException.class,
                () -> order.changeStatusTo(KitchenOrderStatus.PRONTO));
    }

    @Test
    void shouldRejectTransitionFromFinalized() {
        KitchenOrder order = KitchenOrder.builder()
                .orderId("1")
                .status(KitchenOrderStatus.FINALIZADO)
                .createdAt(Instant.now())
                .build();

        assertThrows(InvalidStatusTransitionException.class,
                () -> order.changeStatusTo(KitchenOrderStatus.EM_PREPARACAO));
    }
}