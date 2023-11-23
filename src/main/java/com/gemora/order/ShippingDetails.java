package com.gemora.order;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ShippingDetails {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String postcode;
    private String email;
    private String note;
}
