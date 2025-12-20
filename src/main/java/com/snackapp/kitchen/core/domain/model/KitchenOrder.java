package com.snackapp.kitchen.core.domain.model;


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
}