package com.aragh.promotion.store;

import com.aragh.promotion.Promotion;

import java.util.List;

public interface PromotionStore {

    void save(Promotion promotion);

    List<Promotion> getAllActivePromotions();
}
