package com.aragh.service;

import com.aragh.model.Cart;

import java.math.BigDecimal;

public interface CartCheckoutService {

    /**
     * Calculate the total price of the items in the cart
     * @param cart cart with the items on which totals is calculated
     * @return Total price of the cart
     */
    BigDecimal getCartTotal(Cart cart);
}
