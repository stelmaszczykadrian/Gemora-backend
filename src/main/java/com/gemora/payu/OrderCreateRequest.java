package com.gemora.payu;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @JsonAlias("description")
    private String description;

    @JsonAlias("totalAmount")
    private String totalAmount;
}
