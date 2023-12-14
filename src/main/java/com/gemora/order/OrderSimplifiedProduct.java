package com.gemora.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OrderSimplifiedProduct {
    @NotNull(message = "OrderSimplifiedProduct ID cannot be null")
    @Column(name = "product_id")
    private Integer id;

    @NotEmpty(message = "OrderSimplifiedProduct name cannot be empty")
    @Column(name = "product_name")
    private String name;

    @Min(value = 1, message = "OrderSimplifiedProduct quantity cannot be less than 1")
    @Column(name = "quantity")
    private Integer quantity;

    @Min(value = 0, message = "OrderSimplifiedProduct price cannot be negative")
    @Column(name = "price")
    private Double price;
}
