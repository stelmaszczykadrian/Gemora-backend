package com.gemora.order;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDetails {
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 2, message = "First name must be at least 2 characters long")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 2, message = "Last name must be at least 2 characters long")
    private String lastName;

    @NotEmpty(message = "Address cannot be empty")
    @Size(min = 5, message = "Address must be at least 5 characters long")
    private String address;

    @NotEmpty(message = "City cannot be empty")
    @Size(min = 3, message = "City must be at least 3 characters long")
    private String city;

    @NotEmpty(message = "Postcode cannot be empty")
    private String postcode;

    @Email(message = "Invalid email address")
    private String email;

    @Size(max = 200, message = "Note cannot exceed 200 characters")
    private String note;
}
