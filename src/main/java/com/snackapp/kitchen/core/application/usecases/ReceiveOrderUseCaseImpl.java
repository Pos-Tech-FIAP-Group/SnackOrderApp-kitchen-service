package com.snackapp.kitchen.core.application.usecases;


import com.snackapp.kitchen.core.application.command.ReceiveOrderCommand;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import com.snackapp.kitchen.core.domain.vo.KitchenAddOn;
import com.snackapp.kitchen.core.domain.vo.KitchenOrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final KitchenOrderRepositoryPort kitchenOrderRepositoryPort;

    @Override
    public void receive(ReceiveOrderCommand command) {

        var itens = command.itens().stream()
                .map(i -> new KitchenOrderItem(
                        i.name(),
                        i.quantity(),
                        i.addOns().stream()
                                .map(a -> new KitchenAddOn(a.name(), a.quantity()))
                                .toList()
                ))
                .toList();

        var order = KitchenOrder.builder()
                .orderId(command.orderId())
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .itens(itens)
                .build();

        kitchenOrderRepositoryPort.saveIfAbsent(order);

    }
}