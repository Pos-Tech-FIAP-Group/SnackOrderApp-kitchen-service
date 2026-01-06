package com.snackapp.kitchen.adapters.driven.infra.persistence.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class KitchenOrderEntity {

    private Long orderId;
    private String status;
    private String createdAt;

    @DynamoDbPartitionKey
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    @DynamoDbSecondaryPartitionKey(indexNames = {"gsi_status_createdAt"})
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @DynamoDbSecondarySortKey(indexNames = {"gsi_status_createdAt"})
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}