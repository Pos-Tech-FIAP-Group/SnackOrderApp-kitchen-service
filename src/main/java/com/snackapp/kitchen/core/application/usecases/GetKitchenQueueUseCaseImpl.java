package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetKitchenQueueUseCaseImpl implements GetKitchenQueueUseCase {

    private final KitchenOrderRepositoryPort repository;

    @Override
    public List<KitchenOrder> getQueue(KitchenOrderStatus status) {
        return repository.findByStatus(status);
    }
}