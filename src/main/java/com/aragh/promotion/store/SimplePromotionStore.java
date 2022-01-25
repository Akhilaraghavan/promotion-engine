package com.aragh.promotion.store;

import com.aragh.promotion.PromotionOffer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimplePromotionStore implements PromotionStore {

    private final Map<Integer, PromotionOffer> promotions;

    public SimplePromotionStore() {
        promotions = new LinkedHashMap<>();
    }

    @Override
    public void save(PromotionOffer promotionOffer) {
        promotions.put(promotionOffer.getId(), promotionOffer);
    }

    @Override
    public List<PromotionOffer> getAllActivePromotions() {
        return promotions.values()
                .stream()
                .filter(PromotionOffer::isEnabled)
                .collect(Collectors.toUnmodifiableList());
    }
}
