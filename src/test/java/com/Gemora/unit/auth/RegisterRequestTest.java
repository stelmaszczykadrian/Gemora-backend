package com.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.RegisterRequest;
import com.gemora.user.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GemoraApplication.class)
public class RegisterRequestTest {

    private final String USER_NAME = "John";
    private final String USER_LASTNAME = "Doe";
    private final String USER_PASSWORD = "password123";
    private final Role USER_ROLE = Role.USER;
    private final String USER_EMAIL = "john@example.com";
    private final String DIFFERENT_USER_EMAIL = "jane@example.com";
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRegisterRequest_NoValidationErrors() {
        //given
        RegisterRequest request = getRegisterRequest(USER_EMAIL);

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidRegisterRequest_ValidationError_FirstnameIsBlank() {
        //given
        RegisterRequest request = RegisterRequest.builder()
                .firstname("")
                .lastname(USER_LASTNAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .role(USER_ROLE)
                .build();

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void invalidRegisterRequest_ValidationError_LastnameIsBlank() {
        //given
        RegisterRequest request = RegisterRequest.builder()
                .firstname(USER_NAME)
                .lastname("")
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .role(USER_ROLE)
                .build();

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void invalidRegisterRequest_ValidationError_InvalidEmail() {
        //given
        RegisterRequest request = getRegisterRequest("invalid-email");

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void invalidRegisterRequest_ValidationError_PasswordIsBlank() {
        //given
        RegisterRequest request = RegisterRequest.builder()
                .firstname(USER_NAME)
                .lastname(USER_LASTNAME)
                .email(USER_EMAIL)
                .password("")
                .role(USER_ROLE)
                .build();

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void validRegisterRequest_NoValidationErrors_DefaultRole() {
        //given
        RegisterRequest request = getRegisterRequest(USER_EMAIL);

        //when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        //then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSetRole_AssignedSuccessfully_NewRole() {
        //given
        RegisterRequest request = getRegisterRequest(USER_EMAIL);
        //when
        request.setRole(Role.ADMIN);

        //then
        assertEquals(Role.ADMIN, request.getRole());
    }

    @Test
    void testEquals_ReturnsTrue_ObjectsAreSame() {
        //given
        RegisterRequest request1 = getRegisterRequest(USER_EMAIL);
        RegisterRequest request2 = getRegisterRequest(USER_EMAIL);

        //then
        assertEquals(request1, request2);
    }

    @Test
    void testEquals_ReturnsFalse_EmailsAreDifferent() {
        //given
        RegisterRequest request1 = getRegisterRequest(USER_EMAIL);
        RegisterRequest request2 = getRegisterRequest(DIFFERENT_USER_EMAIL);

        //then
        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCode_ReturnsTrue_ObjectsAreSame() {
        //given
        RegisterRequest request1 = getRegisterRequest(USER_EMAIL);
        RegisterRequest request2 = getRegisterRequest(USER_EMAIL);

        //then
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testHashCode_ReturnsFalse_EmailsAreDifferent() {
        //given
        RegisterRequest request1 = getRegisterRequest(USER_EMAIL);
        RegisterRequest request2 = getRegisterRequest(DIFFERENT_USER_EMAIL);

        //then
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString_ReturnsExpectedString() {
        //given
        RegisterRequest request = getRegisterRequest(USER_EMAIL);

        //then
        String expectedToString = "RegisterRequest(firstname=John, lastname=Doe, email=john@example.com, password=password123, role=USER)";
        assertEquals(expectedToString, request.toString());
    }

    private RegisterRequest getRegisterRequest(String email) {
        return RegisterRequest.builder()
                .firstname(USER_NAME)
                .lastname(USER_LASTNAME)
                .email(email)
                .password(USER_PASSWORD)
                .role(USER_ROLE)
                .build();
    }
}
