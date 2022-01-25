package com.aragh.pricing;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SimplePricingCalculatorTest {

    private SimplePricingCalculator simplePricingCalculator;

    @BeforeEach
    public void beforeEach() {
        simplePricingCalculator = new SimplePricingCalculator();
    }

    @Test
    public void testCartTotalsBeforePromotion() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 1, BigDecimal.valueOf(50)));
        cart.add(Item.of('B', 1, BigDecimal.valueOf(30)));
        cart.add(Item.of('C', 1, BigDecimal.valueOf(20)));
        BigDecimal totals = simplePricingCalculator.getTotals(cart);
        assertEquals(BigDecimal.valueOf(100), totals);

    }

    @Test
    public void testCartTotalsAfterPromotion() {
        Cart cart = new Cart();
        Item itemA = Mockito.mock(Item.class);
        cart.add(itemA);
        Item itemB = Mockito.mock(Item.class);
        cart.add(itemB);
        cart.add(Item.of('C', 1, BigDecimal.valueOf(20)));

        when(itemA.getTotalPriceAfterPromotion()).thenReturn(BigDecimal.valueOf(130));
        when(itemB.getTotalPriceAfterPromotion()).thenReturn(BigDecimal.valueOf(100));

        BigDecimal totals = simplePricingCalculator.getTotals(cart);
        assertEquals(BigDecimal.valueOf(250), totals);
    }
}
