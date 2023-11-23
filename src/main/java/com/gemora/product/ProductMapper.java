package com.gemora.product;

import java.util.Base64;

public class ProductMapper {
    public static ProductDto mapProductToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .manufacturer(product.getManufacturer())
                .description(product.getDescription())
                .category(product.getCategory())
                .image(Base64.getEncoder().encodeToString(product.getImage()))
                .build();
    }
}
