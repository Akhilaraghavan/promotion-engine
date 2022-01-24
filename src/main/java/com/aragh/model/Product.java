package com.aragh.model;

import java.math.BigDecimal;

/**
 * Product represents an SKU with its price
 */
public class Product {
    private final String skuId;
    private final BigDecimal unitPrice;

    private Product(String skuId, BigDecimal unitPrice) {
        this.skuId = skuId;
        this.unitPrice = unitPrice;
    }

    public static Product of(String skuId, BigDecimal unitPrice) {
        return new Product(skuId, unitPrice);
    }

    public String getSkuId() {
        return skuId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
