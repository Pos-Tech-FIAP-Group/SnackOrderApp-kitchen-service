package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.dto.request.OrderReceivedMessage;

public interface ReceiveOrderUseCase {
    void receive(OrderReceivedMessage message);
}
