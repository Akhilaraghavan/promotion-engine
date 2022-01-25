package com.aragh.promotion.store;

import com.aragh.promotion.PromotionOffer;

import java.util.List;

public interface PromotionStore {

    void save(PromotionOffer promotionOffer);

    List<PromotionOffer> getAllActivePromotions();
}
