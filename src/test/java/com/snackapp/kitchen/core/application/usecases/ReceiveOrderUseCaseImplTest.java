package com.snackapp.kitchen.core.application.usecases;



import com.snackapp.kitchen.core.application.command.ReceiveOrderCommand;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceiveOrderUseCaseImplTest {

    @Test
    @DisplayName("Deve salvar o pedido RECEBIDO de forma idempotente com itens e adicionais")
    void deveSalvarPedidoRecebidoDeFormaIdempotenteComItensEAdicionais() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);
        ReceiveOrderUseCaseImpl useCase = new ReceiveOrderUseCaseImpl(repo);

        ReceiveOrderCommand cmd = new ReceiveOrderCommand(
                9000L,
                List.of(
                        new ReceiveOrderCommand.Item(
                                "Coca-Cola",
                                1,
                                List.of(new ReceiveOrderCommand.AddOn("Queijo Cheddar", 2))
                        )
                )
        );

        useCase.receive(cmd);

        ArgumentCaptor<KitchenOrder> captor = ArgumentCaptor.forClass(KitchenOrder.class);
        verify(repo, times(1)).saveIfAbsent(captor.capture());

        KitchenOrder saved = captor.getValue();
        assertEquals(9000L, saved.getOrderId());
        assertEquals(KitchenOrderStatus.RECEBIDO, saved.getStatus());
        assertNotNull(saved.getCreatedAt());

        assertNotNull(saved.getItens());
        assertEquals(1, saved.getItens().size());
        assertEquals("Coca-Cola", saved.getItens().get(0).name());
        assertEquals(1, saved.getItens().get(0).quantity());

        assertNotNull(saved.getItens().get(0).addOns());
        assertEquals(1, saved.getItens().get(0).addOns().size());
        assertEquals("Queijo Cheddar", saved.getItens().get(0).addOns().get(0).name());
        assertEquals(2, saved.getItens().get(0).addOns().get(0).quantity());
    }
}