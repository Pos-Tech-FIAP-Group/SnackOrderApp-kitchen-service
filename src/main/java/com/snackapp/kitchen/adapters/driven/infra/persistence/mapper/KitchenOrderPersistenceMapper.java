package com.snackapp.kitchen.adapters.driven.infra.persistence.mapper;


import com.snackapp.kitchen.adapters.driven.infra.persistence.entity.KitchenAddOnEntity;
import com.snackapp.kitchen.adapters.driven.infra.persistence.entity.KitchenOrderEntity;
import com.snackapp.kitchen.adapters.driven.infra.persistence.entity.KitchenOrderItemEntity;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import com.snackapp.kitchen.core.domain.vo.KitchenAddOn;
import com.snackapp.kitchen.core.domain.vo.KitchenOrderItem;

import java.time.Instant;
import java.util.List;

public class KitchenOrderPersistenceMapper {

    private KitchenOrderPersistenceMapper() {}

    // Domain -> Entity (para salvar no Dynamo)
    public static KitchenOrderEntity toEntity(KitchenOrder domain) {
        KitchenOrderEntity e = new KitchenOrderEntity();
        e.setOrderId(domain.getOrderId());
        e.setStatus(domain.getStatus().name());
        e.setCreatedAt(domain.getCreatedAt().toString());
        e.setItens(mapItensToEntity(domain.getItens()));
        return e;
    }

    // Entity -> Domain (para retornar no GET)
    public static KitchenOrder toDomain(KitchenOrderEntity e) {
        return KitchenOrder.builder()
                .orderId(e.getOrderId())
                .status(KitchenOrderStatus.valueOf(e.getStatus()))
                .createdAt(Instant.parse(e.getCreatedAt()))
                .itens(mapItensToDomain(e.getItens()))
                .build();
    }

    private static List<KitchenOrderItemEntity> mapItensToEntity(List<KitchenOrderItem> itens) {
        if (itens == null) return List.of();

        return itens.stream().map(i -> {
            KitchenOrderItemEntity ie = new KitchenOrderItemEntity();
            ie.setName(i.name());
            ie.setQuantity(i.quantity());
            ie.setAddOns(mapAddOnsToEntity(i.addOns()));
            return ie;
        }).toList();
    }

    private static List<KitchenOrderItem> mapItensToDomain(List<KitchenOrderItemEntity> itens) {
        if (itens == null) return List.of();

        return itens.stream().map(i ->
                new KitchenOrderItem(
                        i.getName(),
                        i.getQuantity() == null ? 0 : i.getQuantity(),
                        mapAddOnsToDomain(i.getAddOns())
                )
        ).toList();
    }

    private static List<KitchenAddOnEntity> mapAddOnsToEntity(List<KitchenAddOn> addOns) {
        if (addOns == null) return List.of();

        return addOns.stream().map(a -> {
            KitchenAddOnEntity ae = new KitchenAddOnEntity();
            ae.setName(a.name());
            ae.setQuantity(a.quantity());
            return ae;
        }).toList();
    }

    private static List<KitchenAddOn> mapAddOnsToDomain(List<KitchenAddOnEntity> addOns) {
        if (addOns == null) return List.of();

        return addOns.stream().map(a ->
                new KitchenAddOn(
                        a.getName(),
                        a.getQuantity() == null ? 0 : a.getQuantity()
                )
        ).toList();
    }
}
