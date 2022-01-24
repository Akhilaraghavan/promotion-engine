package com.aragh.promotion;

import com.aragh.model.Item;

import java.util.List;

public interface Promotion {
    List<String> getPromotionSKUIds();
    void apply(List<Item> items);
    boolean isEnabled();
    void setEnabled(boolean enabled);
    int getId();
}
