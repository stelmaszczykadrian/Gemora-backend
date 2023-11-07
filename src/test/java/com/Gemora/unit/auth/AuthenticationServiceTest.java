package com.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.AuthenticationRequest;
import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.AuthenticationService;
import com.gemora.auth.RegisterRequest;
import com.gemora.config.JwtService;
import com.gemora.security.token.Token;
import com.gemora.security.token.TokenRepository;
import com.gemora.user.Role;
import com.gemora.user.User;
import com.gemora.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    private AuthenticationService authenticationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenRepository tokenRepository;

    private final String JWT_TOKEN = "jwtToken";
    private final String REFRESH_TOKEN = "refreshToken";

    @BeforeEach
    void init() {
        authenticationService = new AuthenticationService(userRepositoryMock, tokenRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    public void register_UserIsRegisteredSuccessfully_WhenNewUserIsCreated(){
        //given
        RegisterRequest registerRequest = createRegisterRequest();
        User expectedUser = createExpectedUser(registerRequest);

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepositoryMock.save(any(User.class))).thenReturn(expectedUser);
        doReturn(JWT_TOKEN).when(jwtService).generateToken(any(User.class));
        doReturn(REFRESH_TOKEN).when(jwtService).generateRefreshToken(any(User.class));
        when(userRepositoryMock.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(expectedUser));

        //when
        AuthenticationResponse response = authenticationService.register(registerRequest);

        //then
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepositoryMock).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));

        assertAuthenticationResponseIsValid(response);
    }

    @Test
    public void authenticate_UserIsAuthenticatedSuccessfully_WhenValidCredentialsAreProvided(){
        //given
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        User user = createTestUser(authenticationRequest);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepositoryMock.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(JWT_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(List.of(new Token()));

        //when
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        //then
        assertAuthenticationResponseIsValid(authenticationResponse);
        verify(tokenRepository, times(1)).saveAll(any(Iterable.class));

    }

    @Test
    public void userExists_ReturnsTrue_ForExistingUser(){
        //given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(user));

        //when
        boolean exists = authenticationService.userExists(email);

        //then
        assertThat(exists).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"non-existing@test.pl"})
    public void userExists_ReturnsFalse_ForNonExistingUser(String email) {
        //given
        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());

        //when
        boolean exists = authenticationService.userExists(email);

        //then
        assertFalse(exists);
    }

    private void assertAuthenticationResponseIsValid(AuthenticationResponse authenticationResponse) {
        assertEquals(JWT_TOKEN, authenticationResponse.getAccessToken());
        assertEquals(REFRESH_TOKEN, authenticationResponse.getRefreshToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Doe");
        registerRequest.setEmail("johndoe@example.com");
        registerRequest.setPassword("password");
        return registerRequest;
    }

    private User createExpectedUser(RegisterRequest registerRequest) {
        return User.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    private User createTestUser(AuthenticationRequest authenticationRequest) {
        return User.builder()
                .firstname("John")
                .lastname("Doe")
                .email(authenticationRequest.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    private AuthenticationRequest createAuthenticationRequest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("johndoe@example.com");
        authenticationRequest.setPassword("password");
        return authenticationRequest;
    }

}
