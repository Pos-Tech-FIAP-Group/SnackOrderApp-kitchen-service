package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.command.ReceiveOrderCommand;

public interface ReceiveOrderUseCase {
    void receive(ReceiveOrderCommand command);
}
