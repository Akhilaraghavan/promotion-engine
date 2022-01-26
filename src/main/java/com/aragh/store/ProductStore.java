package com.aragh.store;

import com.aragh.model.Product;

import java.util.Optional;

public interface ProductStore {
    /**
     * Save product to the store
     * @param product Product
     */
    void saveProduct(Product product);

    /**
     * Find product by the skuId
     * @param skuId  Unique stock keeping unit id
     * @return       Returns Optional of null, if skuId does not exist,
     *               else returns Optional of the Product
     */
    Optional<Product> findBySkuId(Character skuId);
}
