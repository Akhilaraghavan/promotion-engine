package com.aragh.store;

import com.aragh.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductStore {

    void saveProduct(Product product);

    Optional<Product> findBySkuId(Character skuId);
}
