package com.snackapp.kitchen.adapters.driver.amqp.message.mapper;


import com.snackapp.kitchen.adapters.driver.amqp.message.OrderToKitchenMessage;
import com.snackapp.kitchen.core.application.command.ReceiveOrderCommand;

import java.util.List;

public class OrderToKitchenMessageMapper {

    public static ReceiveOrderCommand toCommand(OrderToKitchenMessage msg) {
        List<ReceiveOrderCommand.Item> itens = msg.itens().stream()
                .map(i -> new ReceiveOrderCommand.Item(
                        i.name(),
                        i.quantity(),
                        i.addOns().stream()
                                .map(a -> new ReceiveOrderCommand.AddOn(a.name(), a.quantity()))
                                .toList()
                ))
                .toList();

        return new ReceiveOrderCommand(msg.orderId(), itens);
    }
}