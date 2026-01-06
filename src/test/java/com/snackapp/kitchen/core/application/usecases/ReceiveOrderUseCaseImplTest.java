package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.adapters.driver.api.dto.request.OrderReceivedMessage;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceiveOrderUseCaseImplTest {

    @Test
    void shouldSaveReceivedOrderIdempotently() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);
        ReceiveOrderUseCaseImpl useCase = new ReceiveOrderUseCaseImpl(repo);

        OrderReceivedMessage msg = mock(OrderReceivedMessage.class);
        when(msg.getOrderId()).thenReturn(9000L);

        useCase.receive(msg);

        ArgumentCaptor<KitchenOrder> captor = ArgumentCaptor.forClass(KitchenOrder.class);
        verify(repo, times(1)).saveIfAbsent(captor.capture());

        KitchenOrder saved = captor.getValue();
        assertEquals(9000L, saved.getOrderId());
        assertEquals(KitchenOrderStatus.RECEBIDO, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }
}