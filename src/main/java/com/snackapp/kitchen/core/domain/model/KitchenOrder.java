package com.snackapp.kitchen.core.domain.model;


import com.snackapp.kitchen.core.application.exception.InvalidStatusTransitionException;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.vo.KitchenOrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@Getter
public class KitchenOrder {

    Long orderId;
    KitchenOrderStatus status;
    Instant createdAt;
    List<KitchenOrderItem> itens;


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
                .itens(this.itens)
                .build();
    }
}

