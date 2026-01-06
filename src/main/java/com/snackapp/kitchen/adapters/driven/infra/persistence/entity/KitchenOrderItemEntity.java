package com.snackapp.kitchen.adapters.driven.infra.persistence.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@DynamoDbBean
public class KitchenOrderItemEntity {

    private String name;
    private Integer quantity;
    private List<KitchenAddOnEntity> addOns;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public List<KitchenAddOnEntity> getAddOns() { return addOns; }
    public void setAddOns(List<KitchenAddOnEntity> addOns) { this.addOns = addOns; }
}