package com.Gemora.unit.user;

import com.gemora.GemoraApplication;
import com.gemora.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GemoraApplication.class)
public class UserControllerTest {
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void init() {
        userController = new UserController(userService);
    }

    @Test
    void getCurrentUser_ReturnsUserDto_UserExists() {
        //given
        User user = createUser();

        UserDto expectedUserDto = new UserDto(user);

        when(userService.getUser(user.getEmail())).thenReturn(expectedUserDto);

        //when
        ResponseEntity<UserDto> response = userController.getCurrentUser(user.getEmail());

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedUserDto);
    }

    @Test
    void getCurrentUser_ReturnsNotFoundStatus_ThrowUserNotFoundException() {
        //given
        String userEmail = "nonexistent@example.com";

        when(userService.getUser(userEmail)).thenThrow(UsernameNotFoundException.class);

        // when
        ResponseEntity<UserDto> response = userController.getCurrentUser(userEmail);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
