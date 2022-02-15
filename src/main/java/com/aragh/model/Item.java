package com.aragh.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Item {

    /**
     * product for this Item
     */
    private final Product product;

    /**
     *  The amount of same item in the cart
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

    private Item(Product product, Integer quantity) {
        this.product = Objects.requireNonNull(product, "product is not set");
        this.quantity = Objects.requireNonNull(quantity, "Quantity is not set");
    }

    public static Item of(Product product, Integer quantity) {
        return new Item(product, quantity);
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

    public void updateQuantity(int quantity) {
        this.quantity += quantity;
    }

    public BigDecimal getTotalPriceAfterPromotion() {
        return isPromotionApplied ? totalPriceAfterPromotion : getTotalPrice();
    }

    private BigDecimal getTotalPrice() {
        return product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public Product getProduct() {
        return product;
    }

    public void setTotalPriceAfterPromotion(BigDecimal totalPriceAfterPromotion) {
        this.totalPriceAfterPromotion = totalPriceAfterPromotion;
    }

    public BigDecimal getItemPrice() {
        return product.getUnitPrice();
    }

    public Character getSkuId() {
        return product.getSkuId();
    }

    @Override
    public boolean equals(Object otherItem) {
        if (this == otherItem) {
            return true;
        }
        if (otherItem == null || getClass() != otherItem.getClass()) {
            return false;
        }
        Item item = (Item) otherItem;
        return product.getSkuId().equals(item.getProduct().getSkuId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getSkuId());
    }

    @Override
    public String toString() {
        return "Item{" +
                "skuId='" + product.getSkuId() + '\'' +
                '}';
    }
}
