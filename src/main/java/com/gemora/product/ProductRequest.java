package com.gemora.product;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProductRequest {
    private String name;
    private double price;
    private String manufacturer;
    private String description;
    private String category;
    private String image;
}
