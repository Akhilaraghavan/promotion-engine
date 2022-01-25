package com.aragh.promotion.model;

import com.aragh.model.Item;

import java.util.List;
import java.util.Optional;

public class PromotionSubject {

    private final List<Item> items;

    public PromotionSubject(List<Item> items) {
        this.items = items;
    }

    public Optional<Item> getItem(Character skuId) {
        return items.stream()
                .filter(item -> skuId.equals(item.getSkuId()))
                .findFirst();
    }
}
