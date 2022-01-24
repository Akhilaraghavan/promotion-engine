package com.aragh.promotion.store;

import com.aragh.promotion.model.Promotion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimplePromotionStore implements PromotionStore {

    private final Map<Integer, Promotion> promotions;

    public SimplePromotionStore() {
        promotions = new LinkedHashMap<>();
    }

    @Override
    public void save(Promotion promotion) {
        promotions.put(promotion.getId(), promotion);
    }

    @Override
    public List<Promotion> getAllActivePromotions() {
        return promotions.values()
                .stream()
                .filter(Promotion::isEnabled)
                .collect(Collectors.toUnmodifiableList());
    }
}
