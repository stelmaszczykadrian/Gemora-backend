package com.gemora.order;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderCreateResponse {
    private String data;
    private boolean isSuccess;
}
