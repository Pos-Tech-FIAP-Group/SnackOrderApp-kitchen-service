package com.snackapp.kitchen.core.application.repository;


import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import java.util.List;
import java.util.Optional;

public interface KitchenOrderRepositoryPort {
    void saveIfAbsent(KitchenOrder order);
    List<KitchenOrder> findByStatus(KitchenOrderStatus status);
    Optional<KitchenOrder> findById(String orderId);
    void updateStatus(String orderId, KitchenOrderStatus newStatus);
}
