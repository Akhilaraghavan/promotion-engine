package com.aragh.promotion;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.engine.PromotionEngine;
import com.aragh.promotion.engine.SimplePromotionEngine;
import com.aragh.promotion.store.InMemoryPromotionStore;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.store.InMemoryProductStore;
import com.aragh.store.ProductStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SimplePromotionEngineTest {

    private ProductStore productStore;
    private PromotionStore promotionStore;

    private PromotionEngine promotionEngine;

    @BeforeEach
    public void beforeEach() {
        this.productStore = new InMemoryProductStore();
        this.promotionStore = new InMemoryPromotionStore();
        this.promotionEngine = new SimplePromotionEngine(promotionStore);
        setupProducts();
        setupPromotions();
    }

    private void setupPromotions() {
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('A', 3, BigDecimal.valueOf(130)));
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('B', 2, BigDecimal.valueOf(45)));
        promotionStore.save(new BuyTwoSKUItemsForFixedPrice('C','D', BigDecimal.valueOf(30)));
    }

    private void setupProducts() {
        productStore.saveProduct(Product.of('A', BigDecimal.valueOf(50)));
        productStore.saveProduct(Product.of('B', BigDecimal.valueOf(30)));
        productStore.saveProduct(Product.of('C', BigDecimal.valueOf(20)));
        productStore.saveProduct(Product.of('D', BigDecimal.valueOf(15)));
    }


    private BigDecimal getItemPrice(Character skuId) {
        return productStore.findBySkuId(skuId).orElseThrow().getUnitPrice();
    }

    /**
     *
     * 1*A 50
     * 1*B 30
     * 1*C 20
     */
    @Test
    public void testScenarioA_NoPromotionsApplied() {
        //Assign
        Cart cart = new Cart();
        cart.add(Item.of('A', 1, getItemPrice('A')));
        cart.add(Item.of('B', 1, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));

        //Act
        promotionEngine.applyPromotion(cart);

        //Assert
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
        //Assign
        Cart cart = new Cart();
        cart.add(Item.of('A', 5, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));

        //Act
        promotionEngine.applyPromotion(cart);

        //Assert
        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(230), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemC.getTotalPriceAfterPromotion());
    }

    /**
     *
     * 5*A 100+100*50
     * 5*B 45 + 45 +30
     * 1*C 20
     */
    @Test
    public void testScenarioB_PromotionAppliedOnAAndB_ApplyOnlyOne() {
        // This promotion is not applied, order of insertion is the sort order.
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('A', 2, BigDecimal.valueOf(100)));

        Cart cart = new Cart();
        cart.add(Item.of('A', 5, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));

        promotionEngine.applyPromotion(cart);

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(230), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemC.getTotalPriceAfterPromotion());
    }

    /**
     *
     * 3*A 130
     * 5*B 45 + 45 +30 - 120
     * 1*C -
     * 1*D 30
     */
    @Test
    public void testScenarioC_PromotionAppliedOnAllItems() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 3, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));
        cart.add(Item.of('D', 1, getItemPrice('D')));

        promotionEngine.applyPromotion(cart);

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(130), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertTrue(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(0), itemC.getTotalPriceAfterPromotion());

        Item itemD = cart.getItems().get(3);
        assertTrue(itemD.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(30), itemD.getTotalPriceAfterPromotion());
    }

    /**
     *
     * 3*A 130
     * 5*B 45 + 45 +30 - 120
     * 3*C - 1 item 0, 2*20
     * 1*D 30
     *
     * Promotion rules are mutually exclusive and if one is applied on SKU,
     * the other is ignored.
     */
    @Test
    public void testScenarioC_PromotionAppliedOnAllItems_WithOneNotApplied() {
        // This promotion is not applied
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('C', 2, BigDecimal.valueOf(15)));

        //Assign
        Cart cart = new Cart();
        cart.add(Item.of('A', 3, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 3, getItemPrice('C')));
        cart.add(Item.of('D', 1, getItemPrice('D')));

        //Act
        promotionEngine.applyPromotion(cart);

        //Assert
        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(130), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertTrue(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(40), itemC.getTotalPriceAfterPromotion());

        Item itemD = cart.getItems().get(3);
        assertTrue(itemD.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(30), itemD.getTotalPriceAfterPromotion());
    }

    /**
     *
     * 3*A 130
     * 5*B 45 + 45 +30 - 120
     * 1*C -
     * 2*D 30 + 15 = 45
     */
    @Test
    public void testScenarioC_PromotionAppliedOnAllItems_WithRemainingItemsAfterPromotionForD() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 3, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));
        cart.add(Item.of('D', 2, getItemPrice('D')));

        promotionEngine.applyPromotion(cart);

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(130), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertTrue(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(0), itemC.getTotalPriceAfterPromotion());

        Item itemD = cart.getItems().get(3);
        assertTrue(itemD.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(45), itemD.getTotalPriceAfterPromotion());
    }

    /**
     *
     * 3*A 130
     * 5*B 45 + 45 +30 - 120
     * 1*C -
     * 2*D 30 + 15 = 45
     */
    @Test
    public void testScenarioC_PromotionDisabled_NotApplied() {
        Cart cart = new Cart();
        cart.add(Item.of('A', 3, getItemPrice('A')));
        cart.add(Item.of('B', 5, getItemPrice('B')));
        cart.add(Item.of('C', 1, getItemPrice('C')));
        cart.add(Item.of('D', 2, getItemPrice('D')));

        //Disable the promotion with C&D
        promotionStore.getAllActivePromotions().get(2).setEnabled(false);

        promotionEngine.applyPromotion(cart);

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(130), itemA.getTotalPriceAfterPromotion());

        Item itemB = cart.getItems().get(1);
        assertTrue(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(120), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemC.getTotalPriceAfterPromotion());

        Item itemD = cart.getItems().get(3);
        assertFalse(itemD.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(30), itemD.getTotalPriceAfterPromotion());
    }
}
