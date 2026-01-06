package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;

public interface UpdateKitchenStatusUseCase {
    KitchenOrder updateStatus(Long orderId, KitchenOrderStatus newStatus);
}