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
        when(repo.findById("x")).thenReturn(Optional.empty());

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        assertThrows(OrderNotFoundException.class,
                () -> useCase.updateStatus("x", KitchenOrderStatus.EM_PREPARACAO));

        verify(repo, never()).updateStatus(anyString(), any());
    }

    @Test
    @DisplayName("Deve retornar conflito (InvalidStatusTransitionException) quando a transição for inválida")
    void shouldThrowConflictWhenTransitionIsInvalid() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);

        KitchenOrder current = KitchenOrder.builder()
                .orderId("1")
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .build();

        when(repo.findById("1")).thenReturn(Optional.of(current));

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        assertThrows(InvalidStatusTransitionException.class,
                () -> useCase.updateStatus("1", KitchenOrderStatus.PRONTO));

        verify(repo, never()).updateStatus(anyString(), any());
    }

    @Test
    @DisplayName("Deve atualizar status quando a transição for válida")
    void shouldUpdateStatusWhenTransitionIsValid() {
        KitchenOrderRepositoryPort repo = mock(KitchenOrderRepositoryPort.class);

        KitchenOrder current = KitchenOrder.builder()
                .orderId("1")
                .status(KitchenOrderStatus.RECEBIDO)
                .createdAt(Instant.now())
                .build();

        when(repo.findById("1")).thenReturn(Optional.of(current));

        UpdateKitchenStatusUseCaseImpl useCase = new UpdateKitchenStatusUseCaseImpl(repo);

        KitchenOrder updated = useCase.updateStatus("1", KitchenOrderStatus.EM_PREPARACAO);

        verify(repo, times(1)).updateStatus("1", KitchenOrderStatus.EM_PREPARACAO);
        assertEquals(KitchenOrderStatus.EM_PREPARACAO, updated.getStatus());
    }
}
