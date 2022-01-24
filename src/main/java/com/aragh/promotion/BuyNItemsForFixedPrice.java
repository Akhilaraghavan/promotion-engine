package com.aragh.promotion;

import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.model.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

public class BuyNItemsForFixedPrice implements Promotion {

    private static final Logger LOGGER = Logger.getLogger(BuyNItemsForFixedPrice.class.getName());

    private final int id;
    private final String skuId;
    private final int promotionQuantity;
    private final BigDecimal price;
    private boolean enabled;

    public BuyNItemsForFixedPrice(String skuId, int promotionQuantity, BigDecimal price) {
        this.id = skuId.hashCode();
        this.skuId = skuId;
        this.promotionQuantity = promotionQuantity;
        if (promotionQuantity <= 0) {
            throw new IllegalArgumentException("Number of items should be greater than 0");
        }
        this.price = price;
        this.enabled = true;
    }

    @Override
    public List<String> getPromotionSKUIds() {
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
    public void apply(List<Item> items) {

        Item skuidItem = items.stream().filter(item -> skuId.equals(item.getSkuId()))
                .findFirst()
                .orElseThrow(() ->  new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + items));

        Integer itemQuantity = skuidItem.getQuantity();
        if (itemQuantity < promotionQuantity) {
            LOGGER.fine("Promotion is not applied as the item quantity is less than promotionQuantity");
            return;
        }

        BigDecimal finalPrice = new BigDecimal(0);
        int numberOfItemsLeftWithNoPromotion = itemQuantity % promotionQuantity;
        int applicableForPromotion =  itemQuantity/promotionQuantity;

        finalPrice = finalPrice.add(price.multiply(BigDecimal.valueOf(applicableForPromotion)));
        finalPrice = finalPrice.add(skuidItem.getItemPrice().multiply(BigDecimal.valueOf(numberOfItemsLeftWithNoPromotion)));

        skuidItem.setTotalPriceAfterPromotions(finalPrice);
        skuidItem.promotionApplied();
    }

    @Override
    public String toString() {
        return String.format("buy %s items of %s for %s", promotionQuantity, skuId, price);
    }
}
