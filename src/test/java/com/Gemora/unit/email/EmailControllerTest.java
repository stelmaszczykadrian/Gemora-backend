package com.Gemora.unit.email;

import com.gemora.GemoraApplication;
import com.gemora.email.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class EmailControllerTest {
    private EmailController emailController;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void init() {
        emailController = new EmailController(emailService);
    }

    @Test
    void addEmail_ReturnsCreated_ValidEmail() {
        // given
        Email validEmail = new Email(1, "test@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);

        // when
        ResponseEntity<?> response = emailController.addEmail(validEmail, bindingResult);

        // then
        verify(emailService, times(1)).addEmail(validEmail);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Email added successfully.", response.getBody());
    }

    @Test
    void addEmail_ReturnsBadRequest_InvalidEmail() {
        //given
        Email invalidEmail = new Email(1, "invalidEmail");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<?> response = emailController.addEmail(invalidEmail, bindingResult);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsBadRequest_EmailValidationException() {
        //given
        Email email = new Email(1, "invalid@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(EmailValidationException.class).when(emailService).addEmail(email);

        //when
        ResponseEntity<?> response = emailController.addEmail(email, bindingResult);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsConflict_EmailAlreadyExistsException() {
        //given
        Email email = new Email(1, "existing@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(EmailAlreadyExistsException.class).when(emailService).addEmail(email);

        //when
        ResponseEntity<?> response = emailController.addEmail(email, bindingResult);

        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsInternalServerError_AnyOtherException() {
        //given
        Email email = new Email(1, "any@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(RuntimeException.class).when(emailService).addEmail(email);

        //when
        ResponseEntity<?> response = emailController.addEmail(email, bindingResult);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
