package com.aragh.promotion.engine;

import com.aragh.model.Cart;

public interface PromotionEngine {
    /**
     * Applies the promotion on the cart items.
     * Only enabled/active promotions are applied on the items.
     * Promotions applied are mutually exclusive, when one promotion
     * is applied to the SKU, the other promotions cannot be applied
     * For example : either 2A = 30 or A=A40%
     * @param cart cart
     */
    void applyPromotion(Cart cart);
}
