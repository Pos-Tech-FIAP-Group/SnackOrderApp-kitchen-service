package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.adapters.driver.api.dto.request.OrderReceivedMessage;

public interface ReceiveOrderUseCase {
    void receive(OrderReceivedMessage message);
}
