package com.Gemora.unit.user;

import com.gemora.GemoraApplication;

import com.gemora.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = GemoraApplication.class)
public class UserServiceTest {
    private final String USER_EMAIL = "johndoe@example.com";
    private final String USER_FIRSTNAME = "John";
    private final String USER_LASTNAME= "John";
    private final String USER_PASSWORD= "John";
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @BeforeEach
    void init() {
        userService = new UserService(userRepositoryMock);
    }

    @Test
    public void getUser_ReturnsValidUserDto_UserEmailIsExist() {
        //given
        User userMock = new User(1,USER_FIRSTNAME, USER_LASTNAME, USER_EMAIL, USER_PASSWORD, Role.USER);

        when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(userMock));

        //when
        UserDto userDto = userService.getUser(USER_EMAIL);

        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(USER_EMAIL);
    }

    @Test
    public void getUser_ThrowsUsernameNotFoundException_UserEmailDoesNotExist() {
        //given
        String userEmail = "nonexist@example.com";

        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(null);

        //when & then
        assertThrows(NullPointerException.class, () -> userService.getUser(userEmail));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    public void getUser_ThrowsUsernameNotFoundException_UserEmailIsNullEmptyOrWhitespace(String userEmail) {
        //when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(userEmail));
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    public void getUser_ReturnsValidUserDto_ForAnyUserRole(Role role) {
        //given
        User userMock = new User(1,USER_FIRSTNAME, USER_LASTNAME, USER_EMAIL, USER_PASSWORD, role);

        when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(Optional.of(userMock));

        //when
        UserDto userDto = userService.getUser(USER_EMAIL);

        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(USER_EMAIL);
        assertThat(userDto.getRole()).isEqualTo(role);
    }
}
