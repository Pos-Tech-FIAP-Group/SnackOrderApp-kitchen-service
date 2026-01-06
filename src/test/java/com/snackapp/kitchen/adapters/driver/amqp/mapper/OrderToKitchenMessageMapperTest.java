package com.snackapp.kitchen.adapters.driver.amqp.mapper;

import com.snackapp.kitchen.adapters.driver.amqp.message.AddOnToKitchenMessage;
import com.snackapp.kitchen.adapters.driver.amqp.message.ItemToKitchenMessage;
import com.snackapp.kitchen.adapters.driver.amqp.message.OrderToKitchenMessage;
import com.snackapp.kitchen.core.application.command.ReceiveOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderToKitchenMessageMapperTest {

    @Test
    @DisplayName("Deve mapear mensagem de pedido com itens e adicionais para command")
    void deveMapearMensagemParaCommand() {
        var msg = new OrderToKitchenMessage(
                1L,
                List.of(
                        new ItemToKitchenMessage(
                                "Coca-Cola", 1,
                                List.of(new AddOnToKitchenMessage("Queijo Cheddar", 2))
                        ),
                        new ItemToKitchenMessage(
                                "X-Salada Especial", 2,
                                List.of()
                        )
                )
        );

        ReceiveOrderCommand cmd = OrderToKitchenMessageMapper.toCommand(msg);

        assertEquals(1L, cmd.orderId());
        assertEquals(2, cmd.itens().size());

        assertEquals("Coca-Cola", cmd.itens().get(0).name());
        assertEquals(1, cmd.itens().get(0).quantity());
        assertEquals(1, cmd.itens().get(0).addOns().size());
        assertEquals("Queijo Cheddar", cmd.itens().get(0).addOns().get(0).name());
        assertEquals(2, cmd.itens().get(0).addOns().get(0).quantity());

        assertEquals("X-Salada Especial", cmd.itens().get(1).name());
        assertEquals(2, cmd.itens().get(1).quantity());
        assertTrue(cmd.itens().get(1).addOns().isEmpty());
    }
}