package com.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.AuthenticationRequest;
import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.AuthenticationService;
import com.gemora.auth.RegisterRequest;
import com.gemora.config.JwtService;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import com.gemora.security.token.Token;
import com.gemora.security.token.TokenRepository;
import com.gemora.user.Role;
import com.gemora.user.User;
import com.gemora.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.Gemora.unit.auth.AuthenticationTestHelper.*;
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

    @BeforeEach
    void init() {
        authenticationService = new AuthenticationService(userRepositoryMock, tokenRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    public void register_UserRegisteredSuccessfully_CreateNewUser(){
        //given
        RegisterRequest registerRequest = createRegisterRequest();
        User expectedUser = expectedUser(registerRequest);

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("test");
        when(userRepositoryMock.save(any(User.class))).thenReturn(expectedUser);
        doReturn("sampleAccessToken").when(jwtService).generateToken(any(User.class));
        doReturn("sampleRefreshToken").when(jwtService).generateRefreshToken(any(User.class));
        when(userRepositoryMock.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());

        //when
        AuthenticationResponse response = authenticationService.register(registerRequest);

        //then
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepositoryMock).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));

        assertAuthenticationResponseIsValid(response);
    }

    @Test
    void register_ThrowsEmailValidationException_InvalidEmailFormat() {
        //given
        RegisterRequest request = createRegisterRequest();
        request.setEmail("invalid-email-address");

        //when
        EmailValidationException thrown = assertThrows(EmailValidationException.class,
                () -> authenticationService.register(request));

        //then
        assertThat(thrown.getMessage()).isEqualTo("Invalid email format. Example: gemora@com.pl");
    }

    @Test
    void register_ThrowsEmailAlreadyExistsException_WhenEmailExists() {
        //given
        RegisterRequest request = createRegisterRequest();

        when(userRepositoryMock.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        //when & then
        assertThrows(EmailAlreadyExistsException.class, () -> authenticationService.register(request));

        verify(userRepositoryMock, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    public void authenticate_UserAuthenticatedSuccessfully_ValidCredentialsAreProvided(){
        //given
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        User user = createUser();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepositoryMock.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("sampleAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("sampleRefreshToken");
        when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(List.of(new Token()));

        //when
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        //then
        assertAuthenticationResponseIsValid(authenticationResponse);
        verify(tokenRepository, times(1)).saveAll(any(Iterable.class));

    }

    @Test
    void authenticate_ThrowsUsernameNotFoundException_UserDoesNotExist() {
        //given
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        when(userRepositoryMock.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.empty());

        //when & then
        assertThrows(UsernameNotFoundException.class, () -> {authenticationService.authenticate(authenticationRequest);});

        verify(userRepositoryMock).findByEmail(authenticationRequest.getEmail());

    }

    private void assertAuthenticationResponseIsValid(AuthenticationResponse authenticationResponse) {
        assertEquals("sampleAccessToken", authenticationResponse.getAccessToken());
        assertEquals("sampleRefreshToken", authenticationResponse.getRefreshToken());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    private User expectedUser(RegisterRequest registerRequest) {
        return User.builder()
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .email(registerRequest.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }
}
