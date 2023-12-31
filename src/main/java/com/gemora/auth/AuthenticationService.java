package com.gemora.auth;

import com.gemora.config.JwtService;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import com.gemora.user.Role;
import com.gemora.user.UserRepository;
import com.gemora.security.token.Token;
import com.gemora.security.token.TokenRepository;
import com.gemora.security.token.TokenType;
import com.gemora.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemora.validation.EmailValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${admin.firstname}")
    private String adminFirstName;

    @Value("${admin.lastname}")
    private String adminLastName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AuthenticationResponse register(RegisterRequest request) {
        if (!EmailValidator.isValidEmail(request.getEmail())) {
            log.error(request.getEmail(), "Invalid email format. Example: gemora@com.pl");
            throw new EmailValidationException("Invalid email format. Example: gemora@com.pl");
        }

        if (userExists(request.getEmail())) {
            log.error(request.getEmail(), "Email already exists in the database.");
            throw new EmailAlreadyExistsException("Email already exists in the database.");
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (!userExists(request.getEmail())) {
            log.error(request.getEmail(), "User already do not exists in the database.");
            throw new UsernameNotFoundException("User already do not exists in the database.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private boolean userExists(String email) {
        Optional<User> user = repository.findByEmail(email);
        return user.isPresent();
    }

    public AuthenticationResponse initializeAdminUserAndTokens() {
        User admin = createAdminUser();
        User savedAdmin = repository.save(admin);
        String jwtAdminToken = jwtService.generateToken(admin);
        String refreshAdminToken = jwtService.generateRefreshToken(admin);
        saveUserToken(savedAdmin, jwtAdminToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtAdminToken)
                .refreshToken(refreshAdminToken)
                .build();
    }

    private User createAdminUser() {
        String rawPassword = adminPassword;
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        return User.builder()
                .firstname(adminFirstName)
                .lastname(adminLastName)
                .email(adminEmail)
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();
    }
}
