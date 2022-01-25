package com.aragh.store;

import com.aragh.model.Product;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Product> findBySkuId(Character skuId) {
        return Optional.ofNullable(map.get(skuId));
    }
}
