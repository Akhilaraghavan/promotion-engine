package com.aragh.store;

import com.aragh.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryProductStoreTest {
    private ProductStore productStore;

    @BeforeEach
    public void beforeEach() {
        productStore = new InMemoryProductStore();
    }

    @Test
    public void testSave() {
        assertFalse(productStore.findBySkuId('A').isPresent());

        productStore.saveProduct(Product.of('A', BigDecimal.valueOf(10)));
        productStore.saveProduct(Product.of('B', BigDecimal.valueOf(30)));
        productStore.saveProduct(Product.of('C', BigDecimal.valueOf(50)));
        productStore.saveProduct(Product.of('D', BigDecimal.valueOf(70)));

        Optional<Product> optionalProduct = productStore.findBySkuId('A');
        assertTrue(optionalProduct.isPresent());
        assertEquals(BigDecimal.valueOf(10), optionalProduct.get().getUnitPrice());
    }

}
