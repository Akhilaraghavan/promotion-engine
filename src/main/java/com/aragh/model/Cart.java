package com.aragh.model;

import java.util.*;

/**
 * Cart containing the items different products.
 */
public class Cart {
    private final List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void add(Item item) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.get(index).updateQuantity();
            return;
        }
        items.add(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }
}
