package com.aragh.store;

import com.aragh.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductStore {

    void saveProduct(Product product);

    List<Product> getProducts();

    Optional<Product> findBySkuId(String skuId);
}
