package com.aragh.promotion.engine;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.promotion.store.SimplePromotionStore;

import java.util.List;
import java.util.stream.Collectors;

public class SimplePromotionEngine implements PromotionEngine {

    private final PromotionStore promotionStore;

    public SimplePromotionEngine() {
        this.promotionStore = new SimplePromotionStore();
    }

    @Override
    public void applyPromotion(Cart cart) {
        promotionStore.getAllActivePromotions()
            .forEach(promotion -> {
                    List<String> promotionSKUIds = promotion.getPromotionSKUIds();
                    List<Item> itemsToApplyPromotion = cart.getItems().stream()
                            .filter(item -> promotionSKUIds.contains(item.getSkuId()))
                            .collect(Collectors.toList());
                    promotion.apply(itemsToApplyPromotion);
        });
    }
}
