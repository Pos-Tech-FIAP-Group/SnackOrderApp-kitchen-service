package com.snackapp.kitchen.core.application.usecases;


import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;

import java.util.List;

public interface GetKitchenQueueUseCase {
    List<KitchenOrder> getQueue(KitchenOrderStatus status);
}
