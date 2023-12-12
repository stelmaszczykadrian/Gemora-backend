package com.gemora.auth;

import com.gemora.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Firstname cannot be blank")
    private String firstname;

    @NotBlank(message = "Lastname cannot be blank")
    private String lastname;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Builder.Default
    private Role role = Role.USER;
}
