package com.aragh.promotion.engine;

import com.aragh.model.Cart;
import com.aragh.promotion.PromotionOffer;
import com.aragh.promotion.model.PromotionSubject;
import com.aragh.promotion.store.PromotionStore;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimplePromotionEngine implements PromotionEngine {

    private static final Logger LOGGER = Logger.getLogger(SimplePromotionEngine.class.getName());

    private final PromotionStore promotionStore;

    public SimplePromotionEngine(PromotionStore promotionStore) {
        this.promotionStore = promotionStore;
    }

    /**
     * Applies the promotion on the cart items.
     * Only enabled/active promotions are applied on the items.
     * Promotions applied are mutually exclusive, when one promotion
     * is applied to the SKU, the other promotions cannot be applied
     * For example : either 2A = 30 or A=A40%
     * @param cart cart
     */
    @Override
    public void applyPromotion(Cart cart) {
        promotionStore.getAllActivePromotions()
            .forEach(promotion -> {
                PromotionSubject promotionSubject = getSubject(cart, promotion);
                try {
                    promotion.apply(promotionSubject);
                } catch (ItemPromotionMismatchException e) {
                    LOGGER.warning(String.format("Promotion %s could not be applied on subject %s", promotion, promotionSubject));
                }
        });
    }

    private PromotionSubject getSubject(Cart cart, PromotionOffer promotionOffer) {
        List<String> promotionSKUIds = promotionOffer.getPromotionSKUIds();
         return new PromotionSubject(cart.getItems().stream()
                .filter(item -> !item.isPromotionApplied())
                .filter(item -> promotionSKUIds.contains(item.getSkuId()))
                .collect(Collectors.toList()));
    }
}
