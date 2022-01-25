package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.promotion.model.PromotionSubject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class BuyNItemsOfSKUForFixedPrice implements PromotionOffer {

    private static final Logger LOGGER = Logger.getLogger(BuyNItemsOfSKUForFixedPrice.class.getName());

    private final int id;
    private final Character skuId;
    private final int promotionQuantity;
    private final BigDecimal promotionPrice;
    private boolean enabled;

    public BuyNItemsOfSKUForFixedPrice(Character skuId, int promotionQuantity, BigDecimal promotionPrice) {
        this.id = skuId.hashCode();
        this.promotionQuantity = promotionQuantity;
        this.skuId = Objects.requireNonNull(skuId, "Stock keeping unit is not set");
        if (promotionQuantity <= 0) {
            throw new IllegalArgumentException("Number of items should be greater than 0");
        }
        this.promotionPrice = Objects.requireNonNull(promotionPrice, "Promotion promotionPrice is not set");
        if (this.promotionPrice.signum() <= 0) {
            throw new IllegalArgumentException("Promotion price is incorrect. Price should be a positive non-zero number");
        }
        this.enabled = true;
    }

    @Override
    public List<Character> getPromotionSKUIds() {
        return List.of(skuId);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void apply(PromotionSubject subject) {

        Item skuIdItem = getItem(skuId, subject);
        Integer itemQuantity = skuIdItem.getQuantity();

        if (itemQuantity < promotionQuantity) {
            LOGGER.fine("Promotion is not applied as the item quantity is less than promotionQuantity");
            return;
        }

        BigDecimal finalPrice = BigDecimal.valueOf(0);
        final BigDecimal numberOfItemsLeftWithNoPromotion = BigDecimal.valueOf(itemQuantity % promotionQuantity);
        final BigDecimal applicableForPromotion =  BigDecimal.valueOf(itemQuantity/promotionQuantity);

        finalPrice = finalPrice.add(promotionPrice.multiply(applicableForPromotion));
        finalPrice = finalPrice.add(skuIdItem.getItemPrice().multiply(numberOfItemsLeftWithNoPromotion));

        skuIdItem.setTotalPriceAfterPromotion(finalPrice);
        skuIdItem.promotionApplied();
    }

    @Override
    public String toString() {
        return String.format("buy %s items of %s for %s", promotionQuantity, skuId, promotionPrice);
    }
}
