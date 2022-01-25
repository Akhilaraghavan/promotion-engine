package com.aragh.service;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.promotion.engine.PromotionEngine;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class SimpleCartCheckoutService implements CartCheckoutService {

    private static final Logger LOGGER = Logger.getLogger(SimpleCartCheckoutService.class.getName());

    private final PromotionEngine promotionEngine;

    public SimpleCartCheckoutService(PromotionEngine promotionEngine) {
        this.promotionEngine = promotionEngine;
    }

    /**
     * Calculate the total price of the items in the cart.
     * Apply the promotions before calculating the price.
     * @param cart cart with the items on which totals is calculated
     * @return Total price of the cart
     */
    @Override
    public BigDecimal getCartTotal(Cart cart) {
        LOGGER.fine("Calculate totals for the cart checkout process!");
        promotionEngine.applyPromotion(cart);
        return cart.getItems().
                stream()
                .map(Item::getTotalPriceAfterPromotion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
