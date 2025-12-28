package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.exception.OrderNotFoundException;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateKitchenStatusUseCaseImpl implements UpdateKitchenStatusUseCase {

    private final KitchenOrderRepositoryPort repository;

    @Override
    public KitchenOrder updateStatus(String orderId, KitchenOrderStatus newStatus) {
        KitchenOrder current = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        KitchenOrder updated = current.changeStatusTo(newStatus);

        repository.updateStatus(orderId, updated.getStatus());

        return updated;
    }
}
