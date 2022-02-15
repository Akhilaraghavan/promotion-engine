package com.aragh.promotion;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.engine.PromotionEngine;
import com.aragh.promotion.engine.SimplePromotionEngine;
import com.aragh.promotion.store.InMemoryPromotionStore;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.service.ProductService;
import com.aragh.store.InMemoryProductStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SimplePromotionEngineTest {

    private ProductService productService;
    private PromotionStore promotionStore;

    private PromotionEngine promotionEngine;

    @BeforeEach
    public void beforeEach() {
        this.productService = new ProductService(new InMemoryProductStore());
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
        productService.saveProduct(Product.of('A', BigDecimal.valueOf(50)));
        productService.saveProduct(Product.of('B', BigDecimal.valueOf(30)));
        productService.saveProduct(Product.of('C', BigDecimal.valueOf(20)));
        productService.saveProduct(Product.of('D', BigDecimal.valueOf(15)));
    }


    private Product getProduct(Character skuId) {
        return productService.findProductBySkuId(skuId);
    }

    /**
     *
     * 1*A 50
     * 1*B 30
     * 1*C 20
     */
    @Test
    public void testApplyPromotionScenarioA_NoPromotionsApplied() {
        //Assign
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 1));
        cart.add(Item.of(getProduct('B'), 1));
        cart.add(Item.of(getProduct('C'), 1));

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
    public void testApplyPromotionScenarioB_PromotionAppliedOnAAndB() {
        //Assign
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 5));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 1));

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
    public void testApplyPromotionScenarioB_PromotionAppliedOnAAndB_ApplyOnlyOne() {
        // This promotion is not applied, order of insertion is the sort order.
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('A', 2, BigDecimal.valueOf(100)));

        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 5));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 1));

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
    public void testApplyPromotionScenarioC_PromotionAppliedOnAllItems() {
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 3));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 1));
        cart.add(Item.of(getProduct('D'), 1));

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
    public void testApplyPromotionScenario_PromotionAppliedOnAllItems_WithOneNotApplied() {
        // This promotion is not applied
        promotionStore.save(new BuyNItemsOfSKUForFixedPrice('C', 2, BigDecimal.valueOf(15)));

        //Assign
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 3));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 3));
        cart.add(Item.of(getProduct('D'), 1));

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
    public void testApplyPromotionScenario_PromotionAppliedOnAllItems_WithRemainingItemsAfterPromotionForD() {
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 3));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 1));
        cart.add(Item.of(getProduct('D'), 2));


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
     * 1*C - 20
     * 2*D 30
     */
    @Test
    public void testApplyPromotionScenario_PromotionDisabled_NotApplied() {
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 3));
        cart.add(Item.of(getProduct('B'), 5));
        cart.add(Item.of(getProduct('C'), 1));
        cart.add(Item.of(getProduct('D'), 2));

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

    /**
    *
    * 1*A 50
    * 1*B 30
    * 1*C 20
    */
    @Test
    public void testApplyPromotionScenarioD_WithDiscount() {

        promotionStore.save(new BuySKUItemWithDiscount('A', BigDecimal.valueOf(10)));

        //Assign
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 1));
        cart.add(Item.of(getProduct('B'), 1));
        cart.add(Item.of(getProduct('C'), 1));

        //Act
        promotionEngine.applyPromotion(cart);

        Item itemB = cart.getItems().get(1);
        assertFalse(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(30), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemC.getTotalPriceAfterPromotion());

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(0, BigDecimal.valueOf(45.00000).compareTo(itemA.getTotalPriceAfterPromotion()));
    }

    /**
     *
     * 1*A 50
     * 1*B 30
     * 1*C 20
     */
    @Test
    public void testApplyPromotionScenarioD_WithDiscount_Rounding() {

        promotionStore.save(new BuySKUItemWithDiscount('A', BigDecimal.valueOf(10.2)));

        //Assign
        Cart cart = new Cart();
        cart.add(Item.of(getProduct('A'), 1));
        cart.add(Item.of(getProduct('B'), 1));
        cart.add(Item.of(getProduct('C'), 1));

        //Act
        promotionEngine.applyPromotion(cart);

        Item itemB = cart.getItems().get(1);
        assertFalse(itemB.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(30), itemB.getTotalPriceAfterPromotion());

        Item itemC = cart.getItems().get(2);
        assertFalse(itemC.isPromotionApplied());
        assertEquals(BigDecimal.valueOf(20), itemC.getTotalPriceAfterPromotion());

        Item itemA = cart.getItems().get(0);
        assertTrue(itemA.isPromotionApplied());
        assertEquals(0, BigDecimal.valueOf(44.90000).compareTo(itemA.getTotalPriceAfterPromotion()));
    }
}
