package com.gemora.order;

import com.gemora.product.ProductDto;
import com.gemora.user.UserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Integer id;
    private List<ProductDto> products;
    private UserDto user;
    private LocalDateTime orderDateTime;
    private double totalAmount;
}
