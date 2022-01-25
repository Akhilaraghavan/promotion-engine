package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.promotion.model.PromotionSubject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BuyTwoSKUItemsForFixedPrice implements PromotionOffer {

    private final String skuId1;
    private final String skuId2;
    private final BigDecimal promotionPrice;
    private boolean enabled;
    private final int id;

    public BuyTwoSKUItemsForFixedPrice(String skuId1, String skuId2, BigDecimal promotionPrice) {
        this.skuId1 = Objects.requireNonNull(skuId1, "SKU1 id is not provided");
        this.skuId2 = Objects.requireNonNull(skuId2, "SKU2 id is not provided");
        this.promotionPrice = Objects.requireNonNull(promotionPrice, "Price is not set");
        if (this.promotionPrice.signum() <= 0) {
            throw new IllegalArgumentException("Promotion price is incorrect. Price should be a positive non-zero number");
        }
        this.enabled = true;
        this.id = skuId1.hashCode() + skuId2.hashCode();
    }

    @Override
    public List<String> getPromotionSKUIds() {
        return Arrays.asList(skuId1, skuId2);
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

        Item skuId1Item = getSkuId1Item(skuId1, subject);
        Item skuId2Item = getSkuId1Item(skuId2, subject);

        //Get quantity applicable for promotion
        int applicableForPromotion = Math.min(skuId1Item.getQuantity(), skuId2Item.getQuantity());
        BigDecimal promotionAppliedPrice = BigDecimal.valueOf(0);
        promotionAppliedPrice = promotionAppliedPrice.add(promotionPrice.multiply(BigDecimal.valueOf(applicableForPromotion)));

        //Left over from skuId1Item is priced as is for the remaining
        final BigDecimal skuId1ItemsRemainingAfterPromotion = BigDecimal.valueOf(skuId1Item.getQuantity() - applicableForPromotion);
        final BigDecimal skuId1ItemPrice = skuId1Item.getItemPrice();
        final BigDecimal skuId1ItemTotals = skuId1ItemPrice.multiply(skuId1ItemsRemainingAfterPromotion);
        skuId1Item.setTotalPriceAfterPromotion(skuId1ItemTotals);
        skuId1Item.promotionApplied();

        //Left over from skuId2Item added to promotional price
        final BigDecimal skuId2ItemsRemainingAfterPromotion = BigDecimal.valueOf(skuId2Item.getQuantity() - applicableForPromotion);
        final BigDecimal skuId2ItemPrice = skuId2Item.getItemPrice();
        final BigDecimal skuId2ItemTotals = promotionAppliedPrice.add(skuId2ItemPrice.multiply(skuId2ItemsRemainingAfterPromotion));
        skuId2Item.setTotalPriceAfterPromotion(skuId2ItemTotals);
        skuId2Item.promotionApplied();
    }

    @Override
    public String toString() {
        return String.format("buy %s & %s for a %s", skuId1, skuId2, promotionPrice);
    }
}
