package com.snackapp.kitchen.core.application.usecases;


import com.snackapp.kitchen.core.application.dto.request.OrderReceivedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiveOrderUseCaseImpl implements ReceiveOrderUseCase {

    @Override
    public void receive(OrderReceivedMessage message) {
        log.info("UseCase.receive orderId={} items={}", message.getOrderId(), message.getItems().size());
    }
}