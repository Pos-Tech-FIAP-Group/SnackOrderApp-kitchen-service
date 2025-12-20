package com.snackapp.kitchen.adapters.driven.infra.persistence.adapter;



import com.snackapp.kitchen.adapters.driven.infra.persistence.entity.KitchenOrderEntity;
import com.snackapp.kitchen.core.application.repository.KitchenOrderRepositoryPort;
import com.snackapp.kitchen.core.domain.model.KitchenOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Component
@RequiredArgsConstructor
public class KitchenOrderDynamoAdapter implements KitchenOrderRepositoryPort {

    private final DynamoDbEnhancedClient enhancedClient;

    @Value("${aws.dynamodb.table}")
    private String tableName;

    private DynamoDbTable<KitchenOrderEntity> table() {
        return enhancedClient.table(tableName, TableSchema.fromBean(KitchenOrderEntity.class));
    }

    @Override
    public void saveIfAbsent(KitchenOrder order) {
        var entity = new KitchenOrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt().toString());

        try {
            table().putItem(PutItemEnhancedRequest.builder(KitchenOrderEntity.class)
                    .item(entity)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(orderId)")
                            .build())
                    .build());
        } catch (ConditionalCheckFailedException ignored) {
            // idempotência: já existe
        }
    }
}