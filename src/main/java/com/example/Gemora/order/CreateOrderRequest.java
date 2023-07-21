package com.example.Gemora.order;

import com.example.Gemora.product.ProductDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<ProductDto> productDtoList;


}
