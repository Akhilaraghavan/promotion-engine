package com.aragh.promotion.engine;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.promotion.store.SimplePromotionStore;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimplePromotionEngine implements PromotionEngine {

    private static final Logger LOGGER = Logger.getLogger(SimplePromotionEngine.class.getName());

    private final PromotionStore promotionStore;

    public SimplePromotionEngine(PromotionStore promotionStore) {
        this.promotionStore = promotionStore;
    }

    @Override
    public void applyPromotion(Cart cart) {
        promotionStore.getAllActivePromotions()
            .forEach(promotion -> {
                    List<String> promotionSKUIds = promotion.getPromotionSKUIds();
                    List<Item> itemsToApplyPromotion = cart.getItems().stream()
                            .filter(item -> !item.isPromotionApplied())
                            .filter(item -> promotionSKUIds.contains(item.getSkuId()))
                            .collect(Collectors.toList());
                    try {
                        promotion.apply(itemsToApplyPromotion);
                    } catch (ItemPromotionMismatchException e) {
                        LOGGER.warning(String.format("Promotion %s could not be applied on items %s", promotion, itemsToApplyPromotion));
                    }
        });
    }
}
