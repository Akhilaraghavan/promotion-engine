package com.aragh.promotion.store;

import com.aragh.promotion.BuyTwoSKUItemsForFixedPrice;
import com.aragh.promotion.PromotionOffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryPromotionStoreTest {

    private PromotionStore promotionStore;

    @BeforeEach
    public void beforeEach() {
        promotionStore = new InMemoryPromotionStore();
    }
    @Test
    public void testSave() {
        assertEquals(0, promotionStore.getAllActivePromotions().size());

        promotionStore.save(new BuyTwoSKUItemsForFixedPrice('A','B', BigDecimal.valueOf(100)));

        List<PromotionOffer> allActivePromotions = promotionStore.getAllActivePromotions();
        assertNotNull(allActivePromotions);
        assertEquals(1, allActivePromotions.size());
        assertEquals("buy A & B for a 100", allActivePromotions.get(0).toString());
    }
}
