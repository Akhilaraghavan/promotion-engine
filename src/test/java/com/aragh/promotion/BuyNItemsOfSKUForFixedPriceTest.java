package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.promotion.model.PromotionSubject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuyNItemsOfSKUForFixedPriceTest {

    @Test
    public void testPromotionCreation_ValidateSkuIdIsNotNull_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyNItemsOfSKUForFixedPrice(null, 10, BigDecimal.valueOf(10)));
    }

    @Test
    public void testPromotionCreation_ValidateQuantityIsNegative() {
        assertThrows(RuntimeException.class, () ->
                new BuyNItemsOfSKUForFixedPrice('A', -12313123, BigDecimal.valueOf(10)));
    }

    @Test
    public void testPromotionCreation_ValidateQuantityIsZero_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyNItemsOfSKUForFixedPrice('A', 0, BigDecimal.valueOf(10)));
    }

    @Test
    public void testPromotionCreation_ValidatePriceIsNull_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyNItemsOfSKUForFixedPrice('A', 1, null));
    }

    @Test
    public void testPromotionCreation_ValidatePriceIsNegative_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyNItemsOfSKUForFixedPrice('A', 1, new BigDecimal(-123556)));
    }

    @Test
    public void applyPromotion_ItemQuantityLessThanPromotionQuantity_PromotionNotApplied() {
        //Assign
        BuyNItemsOfSKUForFixedPrice buyNItemsOfSKUForFixedPrice = new BuyNItemsOfSKUForFixedPrice('A' , 5, new BigDecimal(30));
        Item skuItem = Item.of(Product.of('A' ,  new BigDecimal(10)), 2);
        PromotionSubject promotionSubject = new PromotionSubject(List.of(skuItem));

        //Act
        buyNItemsOfSKUForFixedPrice.apply(promotionSubject);

        //Assert
        assertFalse(skuItem.isPromotionApplied());
        assertEquals(20, skuItem.getTotalPriceAfterPromotion().intValue());
    }

    @Test
    public void applyPromotion_ItemAndPromotionMisMatch_ThrowsException() {

        //Assign
        BuyNItemsOfSKUForFixedPrice buyNItemsOfSKUForFixedPrice = new BuyNItemsOfSKUForFixedPrice('A' , 5, new BigDecimal(30));
        Item skuItem = Item.of(Product.of('B',  new BigDecimal(10)) , 2);
        PromotionSubject promotionSubject = new PromotionSubject(List.of(skuItem));

        //Act and Assert
        assertThrows(ItemPromotionMismatchException.class, () -> buyNItemsOfSKUForFixedPrice.apply(promotionSubject));
    }

    @Test
    public void applyPromotion() {
        //Assign
        BuyNItemsOfSKUForFixedPrice buyNItemsOfSKUForFixedPrice = new BuyNItemsOfSKUForFixedPrice('A' , 5, new BigDecimal(30));
        Item skuItem = Item.of(Product.of('A' , new BigDecimal(10)), 7);
        PromotionSubject promotionSubject = new PromotionSubject(List.of(skuItem));

        //Act
        buyNItemsOfSKUForFixedPrice.apply(promotionSubject);

        //Assert
        assertTrue(skuItem.isPromotionApplied());
        assertEquals(50, skuItem.getTotalPriceAfterPromotion().intValue());
    }
}
