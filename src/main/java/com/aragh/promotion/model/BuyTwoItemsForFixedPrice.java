package com.aragh.promotion.model;

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

        Item firstItem = items.stream().filter(item -> skuId1.equals(item.getSkuId()))
                .findFirst()
                .orElseThrow(() ->  new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + items));

        Item secondItem = items.stream().filter(item -> skuId1.equals(item.getSkuId()))
                .findFirst()
                .orElseThrow(() ->  new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + items));

        int minQuantity = Math.min(firstItem.getQuantity(), secondItem.getQuantity());
        BigDecimal minQuantityPrice = new BigDecimal(0);
        minQuantityPrice = minQuantityPrice.add(price.multiply(new BigDecimal(minQuantity)));

        int firstItemsLeft = firstItem.getQuantity() - minQuantity;
        BigDecimal firstItemTotals = firstItem.getItemPrice().multiply(new BigDecimal(firstItemsLeft));
        firstItem.setTotalPriceAfterPromotions(firstItemTotals);
        firstItem.promotionApplied();

        int secondItemsLeft = secondItem.getQuantity() - minQuantity;
        BigDecimal secondItemTotals = minQuantityPrice.add(firstItem.getItemPrice()
                .multiply(new BigDecimal(secondItemsLeft)));
        secondItem.setTotalPriceAfterPromotions(secondItemTotals);
        secondItem.promotionApplied();
    }
}
