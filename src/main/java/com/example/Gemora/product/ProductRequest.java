package com.example.Gemora.product;

import lombok.Data;
@Data
public class ProductRequest {
    private String name;
    private double price;
    private String manufacturer;
    private String description;
    private String category;
    private String image;
}
