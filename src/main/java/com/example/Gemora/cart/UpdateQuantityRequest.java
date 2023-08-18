package com.example.Gemora.cart;

import lombok.Data;

@Data
public class UpdateQuantityRequest {
    private Integer productId;
    private Integer newQuantity;
}
