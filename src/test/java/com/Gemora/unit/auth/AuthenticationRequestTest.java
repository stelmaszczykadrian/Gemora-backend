package com.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.AuthenticationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GemoraApplication.class)
public class AuthenticationRequestTest {

    private final String EMAIL = "test@example.com";
    private final String DIFFERENT_EMAIL = "test2@example.com";
    private final String PASSWORD = "password123";

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createValidAuthenticationRequest_ValidRequest() {
        //given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        //when
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(authenticationRequest);

        //then
        assertTrue(violations.isEmpty());
    }

    @Test
    void createAuthenticationRequest_InvalidRequest_EmailIsBlank() {
        //given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("")
                .password(PASSWORD)
                .build();

        //when
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(authenticationRequest);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void createAuthenticationRequest_InvalidRequest_PasswordIsBlank() {
        //given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(EMAIL)
                .password("")
                .build();

        //when
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(authenticationRequest);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void createAuthenticationRequest_InvalidRequest_EmailAndPasswordAreBlank() {
        //given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("")
                .password("")
                .build();

        //when
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(authenticationRequest);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void testEquals_ReturnsTrue_ObjectsAreSame() {
        //given
        AuthenticationRequest request1 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        AuthenticationRequest request2 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        //then
        assertEquals(request1, request2);
    }

    @Test
    void testEquals_ReturnsFalse_EmailsAreDifferent() {
        //given
        AuthenticationRequest request1 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        AuthenticationRequest request2 = AuthenticationRequest.builder()
                .email(DIFFERENT_EMAIL)
                .password(PASSWORD)
                .build();

        //then
        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCode_ReturnsTrue_ObjectsAreSame() {
        //given
        AuthenticationRequest request1 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        AuthenticationRequest request2 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        //then
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testHashCode_ReturnsFalse_EmailsAreDifferent() {
        //given
        AuthenticationRequest request1 = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        AuthenticationRequest request2 = AuthenticationRequest.builder()
                .email(DIFFERENT_EMAIL)
                .password(PASSWORD)
                .build();

        //then
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString_ReturnsExpectedString() {
        //given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        //then
        String expectedToString = "AuthenticationRequest(email=test@example.com, password=password123)";
        assertEquals(expectedToString, request.toString());
    }

}
