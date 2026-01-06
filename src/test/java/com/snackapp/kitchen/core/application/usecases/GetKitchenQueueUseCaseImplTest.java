package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetKitchenQueueUseCaseImplTest {

    @Test
    @DisplayName("Deve retornar a fila consultando o repositório por status")
    void shouldReturnQueueFromRepository() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);
        GetKitchenQueueUseCaseImpl useCase = new GetKitchenQueueUseCaseImpl(repo);

        List<KitchenOrder> expected = List.of(
                KitchenOrder.builder().orderId(1L).status(KitchenOrderStatus.RECEBIDO).createdAt(Instant.now()).build()
        );

        when(repo.findByStatus(KitchenOrderStatus.RECEBIDO)).thenReturn(expected);

        List<KitchenOrder> result = useCase.getQueue(KitchenOrderStatus.RECEBIDO);

        assertEquals(1, result.size());
        verify(repo, times(1)).findByStatus(KitchenOrderStatus.RECEBIDO);
    }
}
