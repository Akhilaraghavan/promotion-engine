package com.aragh.service;

import com.aragh.model.Product;
import com.aragh.store.ProductStore;

public class ProductService {

    private final ProductStore productStore;

    public ProductService(ProductStore productStore) {
        this.productStore = productStore;
    }

    public Product findProductBySkuId(Character skuId) {
        return productStore.findBySkuId(skuId).orElseThrow();
    }

    public void saveProduct(Product product) {
        productStore.saveProduct(product);
    }
}
