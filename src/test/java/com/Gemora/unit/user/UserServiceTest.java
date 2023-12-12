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

import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = GemoraApplication.class)
public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @BeforeEach
    void init() {
        userService = new UserService(userRepositoryMock);
    }

    @Test
    public void getUser_ReturnsValidUserDto_UserEmailExist() {
        //given
        User userMock = createUser();

        when(userRepositoryMock.findByEmail(userMock.getEmail())).thenReturn(Optional.of(userMock));

        //when
        UserDto userDto = userService.getUser(userMock.getEmail());

        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(userMock.getEmail());
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
        String userEmail = "johndoe@example.com";

        User userMock = new User(1,"John", "Doe", userEmail, "abcdefg", role);

        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(Optional.of(userMock));

        //when
        UserDto userDto = userService.getUser(userEmail);

        //then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(userEmail);
        assertThat(userDto.getRole()).isEqualTo(role);
    }
}
