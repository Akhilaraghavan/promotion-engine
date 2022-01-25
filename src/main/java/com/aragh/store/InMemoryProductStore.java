package com.aragh.store;

import com.aragh.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductStore implements ProductStore {

    private final Map<Character, Product> map;

    public InMemoryProductStore() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public void saveProduct(Product product) {
        map.put(product.getSkuId(), product);
    }

    @Override
    public List<Product> getProducts() {
        return map.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<Product> findBySkuId(Character skuId) {
        return Optional.ofNullable(map.get(skuId));
    }
}
