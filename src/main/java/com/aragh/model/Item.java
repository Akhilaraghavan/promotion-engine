package com.aragh.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Item {

    /**
     *  Stock keeping unit id of the item is a single character
     */
    private final Character skuId;

    /**
     *  Item Price
     */
    private final BigDecimal itemPrice;

    /**
     *  The number of same item in the cart
     */
    private Integer quantity;

    /**
     *  Total Price after the promotion is applied
     */
    private BigDecimal totalPriceAfterPromotion;

    /**
     * If promotion is applied on this item.
     */
    private boolean isPromotionApplied;

    private Item(Character skuId, Integer quantity, BigDecimal itemPrice) {
        this.skuId = Objects.requireNonNull(skuId, "Stock Keeping unit Id is not set");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is not set");
        this.itemPrice = Objects.requireNonNull(itemPrice, "Item price is not set");
    }

    public static Item of(Character skuId, Integer quantity, BigDecimal itemPrice) {
        return new Item(skuId, quantity, itemPrice);
    }

    public Character getSkuId() {
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

    public BigDecimal getTotalPrice() {
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalPriceAfterPromotion() {
        return isPromotionApplied ? totalPriceAfterPromotion : getTotalPrice();
    }

    public void setTotalPriceAfterPromotion(BigDecimal totalPriceAfterPromotion) {
        this.totalPriceAfterPromotion = totalPriceAfterPromotion;
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
