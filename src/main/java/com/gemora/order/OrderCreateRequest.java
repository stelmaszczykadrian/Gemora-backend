package com.gemora.order;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @JsonAlias("description")
    private String description;

    @JsonAlias("totalAmount")
    private String totalAmount;

    public OrderCreateRequest(String description, String totalAmount) {
        this.description = description;
        this.totalAmount = totalAmount;
    }
}
