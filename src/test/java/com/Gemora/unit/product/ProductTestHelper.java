package com.Gemora.unit.product;

import com.gemora.product.Product;
import com.gemora.product.ProductDto;
import com.gemora.product.ProductRequest;

import java.time.LocalDateTime;
import java.util.Base64;

public class ProductTestHelper {
    private static final byte[] IMAGE_BYTES = new byte[]{1, 2, 3};
    private static final String BASE64_ENCODED_IMAGE = Base64.getEncoder().encodeToString(IMAGE_BYTES);

    public static ProductRequest createProductRequest() {
        return ProductRequest.builder()
                .name("Product name")
                .price(100)
                .category("RINGS")
                .description("Product description")
                .image(BASE64_ENCODED_IMAGE)
                .manufacturer("Product manufacturer")
                .build();
    }

    public static ProductDto createProductDto(int id, String productName, double price, String category) {
        return new ProductDto(id, productName, price, "Product manufacturer", "Product description", category, BASE64_ENCODED_IMAGE);
    }

    public static Product createProduct(int id, String productName, double price, String category, LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        return new Product(id, productName, price, "Product manufacturer", "Product description", category, IMAGE_BYTES, date);
    }
}
