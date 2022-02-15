package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.promotion.model.PromotionSubject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuyTwoSKUItemsForFixedPriceTest {

    @Test
    public void testPromotionCreation_ValidateSkuId1IsNotNull_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyTwoSKUItemsForFixedPrice(null, 'A', BigDecimal.valueOf(10)));
    }

    @Test
    public void testPromotionCreation_ValidateSkuId2IsNotNull_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyTwoSKUItemsForFixedPrice('A', null, BigDecimal.valueOf(10)));
    }

    @Test
    public void testPromotionCreation_ValidatePriceIsNull_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyTwoSKUItemsForFixedPrice('A', 'B', null));
    }

    @Test
    public void testPromotionCreation_ValidatePriceIsNegative_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                new BuyTwoSKUItemsForFixedPrice('A', 'B' , new BigDecimal(-123556)));
    }

    @Test
    public void applyPromotion_ItemAndPromotionMisMatch_OneItemIsNotPresent_ThrowsException() {
        //Assign
        BuyTwoSKUItemsForFixedPrice buyTwoSKUItemsForFixedPrice = new BuyTwoSKUItemsForFixedPrice('A' , 'B', new BigDecimal(30));
        Item skuItem = Item.of(Product.of('B' , new BigDecimal(10)), 2);
        PromotionSubject promotionSubject = new PromotionSubject(List.of(skuItem));

        //Act and Assert
        assertThrows(ItemPromotionMismatchException.class, () -> buyTwoSKUItemsForFixedPrice.apply(promotionSubject));
    }

    @Test
    public void applyPromotion() {
        //Assign
        BuyTwoSKUItemsForFixedPrice buyTwoSKUItemsForFixedPrice = new BuyTwoSKUItemsForFixedPrice('A' , 'B', new BigDecimal(30));
        Item skuItem1 = Item.of(Product.of('B' , new BigDecimal(50)), 2);
        Item skuItem2 = Item.of(Product.of('A' ,  new BigDecimal(50)), 2);
        PromotionSubject promotionSubject = new PromotionSubject(List.of(skuItem1, skuItem2));
        //Act
        buyTwoSKUItemsForFixedPrice.apply(promotionSubject);

        assertTrue(skuItem1.isPromotionApplied());
        assertTrue(skuItem2.isPromotionApplied());
        assertEquals(0, skuItem2.getTotalPriceAfterPromotion().intValue());
        assertEquals(60, skuItem1.getTotalPriceAfterPromotion().intValue());
    }
}
