package com.aragh.promotion;

import com.aragh.model.Item;
import com.aragh.promotion.engine.ItemPromotionMismatchException;
import com.aragh.promotion.model.PromotionSubject;

import java.util.List;

public interface PromotionOffer {
    /**
     * Returns the list of SKUIds of this promotion. The ids
     * determine whether the promotion can be applied on items.
     * @return  list of SKUIds
     */
    List<Character> getPromotionSKUIds();

    /**
     * Apply the promotion on the subject.
     * @param subject PromotionSubject with a list of Items {@link Item}
     */
    void apply(PromotionSubject subject);

    /**
     * Returns if the promotion enabled
     * @return true if enabled, false if disabled
     */
    boolean isEnabled();

    /**
     * Enable or disable the promotion
     * @param enabled true to enable, false to disable
     */
    void setEnabled(boolean enabled);

    /**
     * @return  Returns unique Id of this promotion
     */
    int getId();
}
