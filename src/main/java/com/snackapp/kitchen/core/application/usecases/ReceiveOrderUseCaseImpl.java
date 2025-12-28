package com.snackapp.kitchen.core.application.usecases;


import com.snackapp.kitchen.adapters.driver.api.dto.request.OrderReceivedMessage;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    private final KitchenOrderRepositoryPort kitchenOrderRepositoryPort;

    @Override
    public void receive(OrderReceivedMessage message) {
        var order = KitchenOrder.builder()
                .orderId(message.getOrderId())
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(java.time.Instant.now())
                .build();

        kitchenOrderRepositoryPort.saveIfAbsent(order);

        log.info("Pedido salvo no Dynamo (idempotente). orderId={}", message.getOrderId());
    }
}