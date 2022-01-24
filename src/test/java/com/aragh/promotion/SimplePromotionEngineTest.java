package com.aragh.promotion;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.engine.PromotionEngine;
import com.aragh.promotion.engine.SimplePromotionEngine;
import com.aragh.promotion.model.BuyNItemsForFixedPrice;
import com.aragh.promotion.model.BuyTwoItemsForFixedPrice;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.promotion.store.SimplePromotionStore;
import com.aragh.store.ProductStore;
import com.aragh.store.SimpleInMemoryProductStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SimplePromotionEngineTest {

    private ProductStore productStore;
    private PromotionStore promotionStore;

    private PromotionEngine promotionEngine;

    @BeforeEach
    public void beforeAll() {
        this.productStore = new SimpleInMemoryProductStore();
        this.promotionStore = new SimplePromotionStore();
        this.promotionEngine = new SimplePromotionEngine();
        setupProducts();
        setupPromotions();
    }

    private void setupPromotions() {
        promotionStore.save(new BuyNItemsForFixedPrice("A", 3, BigDecimal.valueOf(130)));
        promotionStore.save(new BuyNItemsForFixedPrice("B", 2, BigDecimal.valueOf(45)));
        promotionStore.save(new BuyTwoItemsForFixedPrice("C","D", BigDecimal.valueOf(30)));
    }

    private void setupProducts() {
        productStore.saveProduct(Product.of("A", BigDecimal.valueOf(50)));
        productStore.saveProduct(Product.of("B", BigDecimal.valueOf(30)));
        productStore.saveProduct(Product.of("C", BigDecimal.valueOf(20)));
        productStore.saveProduct(Product.of("D", BigDecimal.valueOf(15)));
    }

    /**
     *
     * 1*A 50
     * 1*B 30
     * 1*C 20
     */
    @Test
    public void testScenarioA_NoPromotionsApplied() {
        Cart cart = new Cart();
        cart.add(Item.of("A", 1));
        cart.add(Item.of("B", 1));
        cart.add(Item.of("C", 1));

        promotionEngine.applyPromotion(cart);

        cart.getItems().forEach(item -> assertFalse(item.isPromotionApplied()));
    }

    /**
     *
     * 5*A 130+2*50
     * 5*B 45 + 45 +30
     * 1*C 20
     */
    @Test
    public void testScenarioB_PromotionAppliedOnAAndB() {
        Cart cart = new Cart();
        cart.add(Item.of("A", 5));
        cart.add(Item.of("B", 5));
        cart.add(Item.of("C", 1));

        promotionEngine.applyPromotion(cart);

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(230), itemA.getTotalPriceAfterPromotions());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotions());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemA.getTotalPriceAfterPromotions());
    }
}
