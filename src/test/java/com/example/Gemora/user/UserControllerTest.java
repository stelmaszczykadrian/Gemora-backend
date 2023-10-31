package com.example.Gemora.user;

import com.gemora.GemoraApplication;
import com.gemora.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void getCurrentUser_ReturnUserDto_WhenUserExists(){
        //given
        User user = createSampleUser();
        UserDto expectedUserDto = new UserDto(user);

        when(userService.getUser(user.getEmail())).thenReturn(expectedUserDto);

        //when
        UserDto actualUserDto = userController.getCurrentUser(user.getEmail());

        //then
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void getCurrentUser_ThrowsException_UserNotFound() {
        //given
        String userEmail = "nonexistent@example.com";

        //when
        when(userService.getUser(userEmail)).thenThrow(UsernameNotFoundException.class);

        //then
        assertThrows(UsernameNotFoundException.class, () -> userController.getCurrentUser(userEmail));
    }

    private User createSampleUser() {
        Integer userId = 1;
        String userEmail = "test@example.com";
        String userFirstname = "John";
        String userLastname = "Doe";

        return User.builder()
                .id(userId)
                .firstname(userFirstname)
                .lastname(userLastname)
                .email(userEmail)
                .role(Role.USER)
                .build();
    }


}
