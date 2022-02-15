package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.promotion.model.PromotionSubject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class BuySKUItemWithDiscount extends BasePromotionOffer implements PromotionOffer {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    public static final int SCALE = 5;

    private final Character skuId;
    private final BigDecimal discount;

    public BuySKUItemWithDiscount(Character skuId, BigDecimal discount) {
        super(skuId.hashCode() * discount.hashCode(), true);
        this.skuId = Objects.requireNonNull(skuId, "SKUId is not provided");
        this.discount = Objects.requireNonNull(discount, "Discount to be applied on SKU is not provided");
    }

    @Override
    public List<Character> getPromotionSKUIds() {
        return List.of(skuId);
    }

    @Override
    public void apply(PromotionSubject subject) {
        Item item = subject.getItem(skuId)
                .orElseThrow(() -> new ItemPromotionMismatchException(String.format("This promotion %s could not be applied on SKUId %s", this, skuId)));
        BigDecimal afterDiscountTotal = getDiscountedPrice(item.getItemPrice(), item.getQuantity());
        item.setTotalPriceAfterPromotion(afterDiscountTotal);
        item.promotionApplied();
    }

    @Override
    public String toString() {
        return String.format("buy %s item for %s%% off", skuId, discount);
    }

    private BigDecimal getDiscountedPrice(BigDecimal itemPrice, Integer quantity) {
        BigDecimal discountedPrice = itemPrice.multiply(discount).divide(ONE_HUNDRED, SCALE, RoundingMode.CEILING);
        return itemPrice.subtract(discountedPrice).multiply(BigDecimal.valueOf(quantity));
    }
}
