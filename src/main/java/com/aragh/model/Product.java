package com.aragh.model;

import java.math.BigDecimal;

/**
 * Product represents an SKU with its price
 */
public final class Product {
    /**
     * Stock Keeping Unit of the product is a single character
     */
    private final Character skuId;

    /**
     * Unit price
     */
    private final BigDecimal unitPrice;

    private Product(Character skuId, BigDecimal unitPrice) {
        this.skuId = skuId;
        this.unitPrice = unitPrice;
    }

    public static Product of(Character skuId, BigDecimal unitPrice) {
        return new Product(skuId, unitPrice);
    }

    public Character getSkuId() {
        return skuId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
