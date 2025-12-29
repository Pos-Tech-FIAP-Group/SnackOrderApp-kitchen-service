package com.snackapp.kitchen.adapters.driven.infra.persistence.adapter;



import com.snackapp.kitchen.adapters.driven.infra.persistence.entity.KitchenOrderEntity;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.enums.KitchenOrderStatus;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import java.time.Instant;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KitchenOrderDynamoAdapter implements KitchenOrderRepositoryPort {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.table}")
    private String tableName;
    @Value("${aws.dynamodb.statusIndex}")
    private String statusIndexName;

    private DynamoDbTable<KitchenOrderEntity> table() {
        return enhancedClient.table(tableName, TableSchema.fromBean(KitchenOrderEntity.class));
    }

    @Override
    public void saveIfAbsent(KitchenOrder order) {
        var entity = new KitchenOrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt().toString());

            table().putItem(PutItemEnhancedRequest.builder(KitchenOrderEntity.class)
                    .item(entity)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(orderId)")
                            .build())
                    .build());

    }

    @Override
    public List<KitchenOrder> findByStatus(KitchenOrderStatus status) {
        DynamoDbIndex<KitchenOrderEntity> index = table().index(statusIndexName);

        QueryConditional cond = QueryConditional.keyEqualTo(k -> k.partitionValue(status.name()));

        return index.query(r -> r.queryConditional(cond))
                .stream()
                .flatMap(page -> page.items().stream())
                .map(e -> KitchenOrder.builder()
                        .orderId(e.getOrderId())
                        .status(KitchenOrderStatus.valueOf(e.getStatus()))
                        .createdAt(Instant.parse(e.getCreatedAt()))
                        .build())
                .toList();
    }

    @Override
    public Optional<KitchenOrder> findById(String orderId) {
        KitchenOrderEntity entity = table().getItem(r -> r.key(k -> k.partitionValue(orderId)));
        if (entity == null) return Optional.empty();

        return Optional.of(KitchenOrder.builder()
                .orderId(entity.getOrderId())
                .status(KitchenOrderStatus.valueOf(entity.getStatus()))
                .createdAt(Instant.parse(entity.getCreatedAt()))
                .build());
    }

    @Override
    public void updateStatus(String orderId, KitchenOrderStatus newStatus) {
        KitchenOrderEntity entity = table().getItem(r -> r.key(k -> k.partitionValue(orderId)));
        if (entity == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + orderId);
        }

        entity.setStatus(newStatus.name());
        table().updateItem(entity);
    }
}