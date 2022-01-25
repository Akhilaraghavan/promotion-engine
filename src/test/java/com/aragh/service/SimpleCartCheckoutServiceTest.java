package com.aragh.service;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.promotion.engine.PromotionEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SimpleCartCheckoutServiceTest {

    private SimpleCartCheckoutService simplePricingCalculator;
    private PromotionEngine promotionEngine;

    @BeforeEach
    public void beforeEach() {
        promotionEngine = Mockito.mock(PromotionEngine.class);
        simplePricingCalculator = new SimpleCartCheckoutService(promotionEngine);
    }

    @Test
    public void testCartTotalsBeforePromotion() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 1, BigDecimal.valueOf(50)));
        cart.add(Item.of('B', 1, BigDecimal.valueOf(30)));
        cart.add(Item.of('C', 1, BigDecimal.valueOf(20)));
        doNothing().when(promotionEngine).applyPromotion(ArgumentMatchers.any(Cart.class));
        BigDecimal totals = simplePricingCalculator.getCartTotal(cart);
        assertEquals(BigDecimal.valueOf(100), totals);

    }

    @Test
    public void testCartTotalsAfterPromotion() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 1, BigDecimal.valueOf(50)));
        cart.add(Item.of('B', 1, BigDecimal.valueOf(30)));
        cart.add(Item.of('C', 1, BigDecimal.valueOf(20)));

        doAnswer(invocation -> {
            Cart pCart = invocation.getArgument(0);
            List<Item> items = pCart.getItems();
            items.get(0).setTotalPriceAfterPromotion(BigDecimal.valueOf(130));
            items.get(0).promotionApplied();
            items.get(1).setTotalPriceAfterPromotion(BigDecimal.valueOf(100));
            items.get(1).promotionApplied();
            return null;
        }).when(promotionEngine).applyPromotion(ArgumentMatchers.any(Cart.class));

        BigDecimal totals = simplePricingCalculator.getCartTotal(cart);
        assertEquals(BigDecimal.valueOf(250), totals);
    }

    @Test
    public void testCartTotalsAfterPromotion_AddSameItemToCart() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 1, BigDecimal.valueOf(50)));
        cart.add(Item.of('B', 1, BigDecimal.valueOf(30)));
        cart.add(Item.of('C', 1, BigDecimal.valueOf(20)));
        cart.add(Item.of('A', 3, BigDecimal.valueOf(50)));

        assertEquals(3, cart.getItems().size());
        assertEquals(4, cart.getItems().get(0).getQuantity());

        doAnswer(invocation -> {
            Cart pCart = invocation.getArgument(0);
            List<Item> items = pCart.getItems();
            items.get(0).setTotalPriceAfterPromotion(BigDecimal.valueOf(130));
            items.get(0).promotionApplied();
            items.get(1).setTotalPriceAfterPromotion(BigDecimal.valueOf(100));
            items.get(1).promotionApplied();
            return null;
        }).when(promotionEngine).applyPromotion(ArgumentMatchers.any(Cart.class));

        BigDecimal totals = simplePricingCalculator.getCartTotal(cart);
        assertEquals(BigDecimal.valueOf(250), totals);
    }
}