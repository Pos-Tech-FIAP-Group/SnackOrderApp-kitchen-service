package com.snackapp.kitchen.core.application.usecases;

import com.snackapp.kitchen.core.application.exception.InvalidStatusTransitionException;
import com.snackapp.kitchen.core.application.exception.OrderNotFoundException;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateKitchenStatusUseCaseImplTest {

    @Test
    @DisplayName("Deve retornar 404 (OrderNotFoundException) quando o pedido não existir")
    void shouldThrowNotFoundWhenOrderDoesNotExist() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);
        when(repo.findById(999999999L)).thenReturn(Optional.empty());

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        assertThrows(OrderNotFoundException.class,
                () -> useCase.updateStatus(999999999L, KitchenOrderStatus.EM_PREPARACAO));

        verify(repo, never()).updateStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Deve retornar conflito (InvalidStatusTransitionException) quando a transição for inválida")
    void shouldThrowConflictWhenTransitionIsInvalid() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);

        KitchenOrder current = KitchenOrder.builder()
                .orderId(1L)
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(current));

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        assertThrows(InvalidStatusTransitionException.class,
                () -> useCase.updateStatus(1L, KitchenOrderStatus.PRONTO));

        verify(repo, never()).updateStatus(anyLong(), any());
    }

    @Test
    @DisplayName("Deve atualizar status quando a transição for válida")
    void shouldUpdateStatusWhenTransitionIsValid() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);

        KitchenOrder current = KitchenOrder.builder()
                .orderId(1L)
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .build();

        when(repo.findById(1L)).thenReturn(Optional.of(current));

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        KitchenOrder updated = useCase.updateStatus(1L, KitchenOrderStatus.EM_PREPARACAO);

        verify(repo, times(1)).updateStatus(1L, KitchenOrderStatus.EM_PREPARACAO);
        assertEquals(KitchenOrderStatus.EM_PREPARACAO, updated.getStatus());
    }
}
