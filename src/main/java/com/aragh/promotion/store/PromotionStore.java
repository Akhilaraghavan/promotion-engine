package com.aragh.promotion.store;

import com.aragh.promotion.model.Promotion;

import java.util.List;

public interface PromotionStore {

    void save(Promotion promotion);

    List<Promotion> getAllActivePromotions();
}
