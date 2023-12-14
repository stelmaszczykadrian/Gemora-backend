package com.Gemora.e2e.user;

import com.gemora.order.OrderRepository;
import com.gemora.security.token.TokenRepository;
import com.gemora.user.Role;
import com.gemora.user.User;
import com.gemora.user.UserDto;
import com.gemora.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserE2ETest {
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
    public void getUserByEmail_ReturnsUserDto_UserExist() {
        //given
        String userEmail = "test@gmail.com";

        String baseUrl = "http://localhost:" + port + "/api/users/profile/" + userEmail;

        User user = new User(1,"John", "Doe", userEmail ,"abcdef", Role.USER);
        userRepository.save(user);

        //when
        ResponseEntity<UserDto> responseEntity = restTemplate.getForEntity(baseUrl, UserDto.class);

        //then
        UserDto userDto = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(userDto);
        assertEquals("John", userDto.getFirstname());
        assertEquals(userEmail, userDto.getEmail());
    }
}
