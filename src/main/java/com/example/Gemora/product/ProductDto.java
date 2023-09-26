package com.example.Gemora.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private Integer id;
    private String name;
    private double price;
    private String manufacturer;
    private String description;
    private String category;
    private String image;
}
