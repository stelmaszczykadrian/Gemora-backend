package com.example.Gemora.payu;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;


@Data
public class OrderCreateRequest {
    private String continueUrl;
    @JsonAlias("customerIp")
    private String customerIp;

    @JsonAlias("merchantPosId")
    private String merchantPosId;
    @JsonAlias("description")
    private String description;
    @JsonAlias("currencyCode")
    private String currencyCode;
    @JsonAlias("totalAmount")
    private String totalAmount;
}
