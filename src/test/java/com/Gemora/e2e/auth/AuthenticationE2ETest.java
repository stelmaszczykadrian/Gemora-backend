package com.Gemora.e2e.auth;

import com.gemora.auth.AuthenticationRequest;
import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.RegisterRequest;
import com.gemora.security.token.TokenRepository;
import com.gemora.user.Role;
import com.gemora.user.User;
import com.gemora.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @AfterEach
    void setUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_ReturnsOkStatusAndExpectedResponse_ValidUserData() {
        //given
        String baseUrl = "http://localhost:" + port + "/api/v1/auth/register";

        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("johndoe@gmail.com")
                .password("test")
                .build();

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity(baseUrl, registerRequest, AuthenticationResponse.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        AuthenticationResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }

    @Test
    void authenticate_ReturnsOkStatusAndExpectedResponse_ValidUserData() {
        // given
        String baseUrl = "http://localhost:" + port + "/api/v1/auth/authenticate";

        String encodedPassword = new BCryptPasswordEncoder().encode("test");

        User user = new User(2, "John", "Doe", "johny@gmail.com", encodedPassword, Role.USER);

        userRepository.save(user);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("johny@gmail.com", "test");

        // when
        ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity(baseUrl, authenticationRequest, AuthenticationResponse.class);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AuthenticationResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }
}
