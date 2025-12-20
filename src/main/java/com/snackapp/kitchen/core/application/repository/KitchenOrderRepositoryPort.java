package com.snackapp.kitchen.core.application.repository;


import com.snackapp.kitchen.core.domain.model.KitchenOrder;

public interface KitchenOrderRepositoryPort {
    void saveIfAbsent(KitchenOrder order);
}