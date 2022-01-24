package com.aragh.pricing;

import com.aragh.model.Cart;

import java.math.BigDecimal;

public interface PricingCalculator {

    BigDecimal getTotals(Cart cart);
}
