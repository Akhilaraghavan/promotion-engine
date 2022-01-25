package com.aragh.pricing;

import com.aragh.model.Cart;
import com.aragh.model.Item;

import java.math.BigDecimal;

public class SimplePricingCalculator implements PricingCalculator {

    @Override
    public BigDecimal getTotals(Cart cart) {
        return cart.getItems().
                stream()
                .map(Item::getTotalPriceAfterPromotion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
