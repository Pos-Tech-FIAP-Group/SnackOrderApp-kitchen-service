package com.snackapp.kitchen.core.domain.vo;


import java.util.List;

public record KitchenOrderItem(String name, int quantity, List<KitchenAddOn> addOns) {}