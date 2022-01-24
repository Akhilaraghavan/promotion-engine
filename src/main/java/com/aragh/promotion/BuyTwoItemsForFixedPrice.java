package com.aragh.promotion;

import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.model.Item;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BuyTwoItemsForFixedPrice implements Promotion {

    private final String skuId1;
    private final String skuId2;
    private final BigDecimal price;
    private boolean enabled;
    private final int id;

    public BuyTwoItemsForFixedPrice(String skuId1, String skuId2, BigDecimal price) {
        this.skuId1 = Objects.requireNonNull(skuId1, "SKU1 id is not provided");
        this.skuId2 = Objects.requireNonNull(skuId2, "SKU2 id is not provided");
        this.price = Objects.requireNonNull(price, "Price is not set");
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
    public String toString() {
        return String.format("buy %s & %s for a %s", skuId1, skuId2, price);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void apply(List<Item> items) {

        Item skuId1Item = items.stream().filter(item -> skuId1.equals(item.getSkuId()))
                .findFirst()
                .orElseThrow(() ->  new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + items));

        Item skuId2Item = items.stream().filter(item -> skuId2.equals(item.getSkuId()))
                .findFirst()
                .orElseThrow(() ->  new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + items));

        //Get quantity applicable for promotion
        int applicableForPromotion = Math.min(skuId1Item.getQuantity(), skuId2Item.getQuantity());
        BigDecimal promotionAppliedPrice = new BigDecimal(0);
        promotionAppliedPrice = promotionAppliedPrice.add(price.multiply(new BigDecimal(applicableForPromotion)));

        //Left over from is priced as is for the remaining
        int firstItemsRemainingAfterPromotion = skuId1Item.getQuantity() - applicableForPromotion;
        BigDecimal firstItemTotals = skuId1Item.getItemPrice().multiply(new BigDecimal(firstItemsRemainingAfterPromotion));
        skuId1Item.setTotalPriceAfterPromotions(firstItemTotals);
        skuId1Item.promotionApplied();

        //Left over from right added to promotional price (cost of C&D)
        int secondItemsRemainingAfterPromotion = skuId2Item.getQuantity() - applicableForPromotion;
        BigDecimal secondItemTotals = promotionAppliedPrice.add(skuId2Item.getItemPrice()
                .multiply(new BigDecimal(secondItemsRemainingAfterPromotion)));
        skuId2Item.setTotalPriceAfterPromotions(secondItemTotals);
        skuId2Item.promotionApplied();
    }
}
