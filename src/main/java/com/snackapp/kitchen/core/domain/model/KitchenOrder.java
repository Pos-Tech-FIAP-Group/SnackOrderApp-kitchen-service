package com.snackapp.kitchen.core.domain.model;


import com.snackapp.kitchen.core.application.exception.InvalidStatusTransitionException;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class KitchenOrder {

    String orderId;
    KitchenOrderStatus status;
    Instant createdAt;


    public KitchenOrder changeStatusTo(KitchenOrderStatus next) {
        if (this.status == next) return this;

        boolean ok = switch (this.status) {
            case RECEBIDO -> next == KitchenOrderStatus.EM_PREPARACAO;
            case EM_PREPARACAO -> next == KitchenOrderStatus.PRONTO;
            case PRONTO -> next == KitchenOrderStatus.FINALIZADO;
            case FINALIZADO -> false;
        };

        if (!ok) {
            throw new InvalidStatusTransitionException(this.status, next);
        }

        return KitchenOrder.builder()
                .orderId(this.orderId)
                .status(next)
                .createdAt(this.createdAt)
                .build();
    }
}

