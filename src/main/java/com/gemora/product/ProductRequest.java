package com.gemora.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull(message = "Product price cannot be null")
    @PositiveOrZero(message = "Product price must be non-negative number")
    private double price;

    @NotBlank(message = "Product manufacturer cannot be blank")
    private String manufacturer;

    @NotBlank(message = "Product description cannot be blank")
    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Product category cannot be blank")
    private String category;

    @NotBlank(message = "Product image cannot be blank")
    private String image;
}
