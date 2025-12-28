package com.snackapp.kitchen.adapters.driver.api.controller;

import com.snackapp.kitchen.adapters.driver.api.dto.request.UpdateKitchenStatusRequest;
import com.snackapp.kitchen.core.application.exception.InvalidKitchenStatusException;
import com.snackapp.kitchen.core.application.usecases.GetKitchenQueueUseCase;
import com.snackapp.kitchen.core.application.usecases.UpdateKitchenStatusUseCase;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kitchen/orders")
@RequiredArgsConstructor
public class KitchenOrderController {

    private final GetKitchenQueueUseCase getKitchenQueueUseCase;
    private final UpdateKitchenStatusUseCase updateKitchenStatusUseCase;

    @GetMapping
    public List<KitchenOrder> queue(@RequestParam KitchenOrderStatus status) {
        return getKitchenQueueUseCase.getQueue(status);
    }

    @PatchMapping("/{orderId}/status")
    public KitchenOrder updateStatus(@PathVariable String orderId,
                                     @RequestBody @Valid UpdateKitchenStatusRequest req) {
        KitchenOrderStatus newStatus;
        try {
            newStatus = KitchenOrderStatus.valueOf(req.status());
        } catch (Exception e) {
            throw new InvalidKitchenStatusException(req.status());
        }
        return updateKitchenStatusUseCase.updateStatus(orderId, newStatus);
    }
}