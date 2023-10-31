package com.gemora.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Integer id;
    private String name;
    private double price;
    private String manufacturer;
    private String description;
    private String category;
    private String image;

//    public ProductDto(Integer id, String name, double price, String manufacturer, String description, String category, String image) {
//        this.id = id;
//        this.name = name;
//        this.price = price;
//        this.manufacturer = manufacturer;
//        this.description = description;
//        this.category = category;
//        this.image = image;
//    }
}
