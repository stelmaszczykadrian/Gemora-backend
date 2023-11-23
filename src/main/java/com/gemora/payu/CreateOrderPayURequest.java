package com.gemora.payu;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateOrderPayURequest {
    private String continueUrl;
    private String customerIp;
    private String merchantPosId;
    @JsonAlias("description")
    private String description;
    private String currencyCode;
    @JsonAlias("totalAmount")
    private String totalAmount;
}
