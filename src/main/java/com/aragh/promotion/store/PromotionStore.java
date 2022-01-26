package com.aragh.promotion.store;

import com.aragh.promotion.PromotionOffer;

import java.util.List;

public interface PromotionStore {

    /**
     * Save PromotionOffer to the store
     * @param promotionOffer PromotionOffer
     */
    void save(PromotionOffer promotionOffer);

    /**
     * @return Returns the list of all active/enabled promotions
     */
    List<PromotionOffer> getAllActivePromotions();
}
