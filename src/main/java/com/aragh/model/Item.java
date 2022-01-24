package com.aragh.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Item {

    private final String skuId;
    private Integer quantity;
    private BigDecimal itemPrice;
    private BigDecimal totalPriceAfterPromotions;
    private boolean isPromotionApplied;

    private Item(String skuId, Integer quantity) {
        this.skuId = Objects.requireNonNull(skuId, "Stock Keeping unit Id is not set");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is not set");
    }

    public static Item of(String skuId, Integer quantity) {
        return new Item(skuId, quantity);
    }

    public String getSkuId() {
        return skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void promotionApplied() {
        isPromotionApplied = true;
    }

    public boolean isPromotionApplied() {
        return isPromotionApplied;
    }

    public void updateQuantity() {
        quantity += 1;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getTotalPrice() {
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalPriceAfterPromotions() {
        return isPromotionApplied ? totalPriceAfterPromotions : getTotalPrice();
    }

    public void setTotalPriceAfterPromotions(BigDecimal totalPriceAfterPromotions) {
        this.totalPriceAfterPromotions = totalPriceAfterPromotions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return skuId.equals(item.skuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId);
    }

    @Override
    public String toString() {
        return "Item{" +
                "skuId='" + skuId + '\'' +
                '}';
    }
}
