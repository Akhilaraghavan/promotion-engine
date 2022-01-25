package com.aragh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart containing the items different products.
 */
public class Cart {
    private final List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    /**
     * Add to cart if the item does not exist
     * if item exists, update quantity.
     * @param item item
     */
    public void add(Item item) {
        if (items.contains(item)) {
            Item existingItem =items.get(items.indexOf(item));
            existingItem.updateQuantity(item.getQuantity());
            return;
        }
        items.add(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }
}
