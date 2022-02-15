package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.promotion.model.PromotionSubject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BuyTwoSKUItemsForFixedPrice extends BasePromotionOffer implements PromotionOffer {

    private final Character skuId1;
    private final Character skuId2;
    private final BigDecimal promotionPrice;

    public BuyTwoSKUItemsForFixedPrice(Character skuId1, Character skuId2, BigDecimal promotionPrice) {
        super(skuId1.hashCode() + skuId2.hashCode(), true);
        this.skuId1 = Objects.requireNonNull(skuId1, "SKU1 id is not provided");
        this.skuId2 = Objects.requireNonNull(skuId2, "SKU2 id is not provided");
        this.promotionPrice = Objects.requireNonNull(promotionPrice, "Price is not set");
        if (this.promotionPrice.signum() <= 0) {
            throw new IllegalArgumentException("Promotion price is incorrect. Price should be a positive non-zero number");
        }
    }

    /**
     * Apply promotion for the one of the SKU. If there is remaining quantity,
     * the normal item price is applied on those and the total price
     * is calculated. For a given SKU its either 2A = 30 or A=A40%
     * @param subject PromotionSubject with a list of Items {@link Item}
     */
    @Override
    public void apply(PromotionSubject subject) {

        Item skuId1Item = subject.getItem(skuId1)
                .orElseThrow(() -> new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + subject));
        Item skuId2Item = subject.getItem(skuId2)
                .orElseThrow(() -> new ItemPromotionMismatchException("Promotion " + this + " cannot be applied on item " + subject));

        //Get quantity applicable for promotion
        int applicableForPromotion = Math.min(skuId1Item.getQuantity(), skuId2Item.getQuantity());
        BigDecimal promotionAppliedPrice = promotionPrice.multiply(BigDecimal.valueOf(applicableForPromotion));

        //Remaining from skuId1Item is priced on the unit price, remaining items is greater or equal to promotion applied items
        final int skuId1ItemsRemainingAfterPromotion = skuId1Item.getQuantity() - applicableForPromotion;
        final BigDecimal skuId1ItemTotals = skuId1Item.getItemPrice()
                .multiply(BigDecimal.valueOf(skuId1ItemsRemainingAfterPromotion));
        skuId1Item.setTotalPriceAfterPromotion(skuId1ItemTotals);
        skuId1Item.promotionApplied();

        //Remaining from skuId2Item added to promotionAppliedPrice, remaining items is greater or equal to promotion applied items
        final int skuId2ItemsRemainingAfterPromotion = skuId2Item.getQuantity() - applicableForPromotion;
        final BigDecimal skuId2ItemTotals = promotionAppliedPrice.add(skuId2Item.getItemPrice()
                .multiply(BigDecimal.valueOf(skuId2ItemsRemainingAfterPromotion)));
        skuId2Item.setTotalPriceAfterPromotion(skuId2ItemTotals);
        skuId2Item.promotionApplied();
    }

    @Override
    public List<Character> getPromotionSKUIds() {
        return Arrays.asList(skuId1, skuId2);
    }

    @Override
    public String toString() {
        return String.format("buy %s & %s for a %s", skuId1, skuId2, promotionPrice);
    }
}
