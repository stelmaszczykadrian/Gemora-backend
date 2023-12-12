package com.Gemora.unit.auth;

import com.gemora.auth.AuthenticationRequest;
import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.RegisterRequest;
import com.gemora.user.Role;
import com.gemora.user.User;

public class AuthenticationTestHelper {
    public static RegisterRequest createRegisterRequest(){
        return RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("johndoe@gmail.com")
                .role(Role.USER)
                .password("test")
                .build();
    }

    public static AuthenticationResponse createAuthenticationResponse() {
        return AuthenticationResponse.builder()
                .accessToken("sampleAccessToken")
                .refreshToken("sampleRefreshToken")
                .build();
    }

    public static AuthenticationRequest createAuthenticationRequest() {
        return AuthenticationRequest.builder()
                .email("johndoe@gmail.com")
                .password("abcdef")
                .build();
    }

    public static User createUser() {
        return new User(1,"John", "Doe", "johndoe@gmail.com", "abcdef", Role.USER);
    }
}
