package com.aragh.store;

import com.aragh.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleInMemoryProductStore implements ProductStore {

    private final Map<String, Product> map;

    public SimpleInMemoryProductStore() {
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
    public Optional<Product> findBySkuId(String skuId) {
        return Optional.ofNullable(map.get(skuId));
    }
}
