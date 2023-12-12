package com.Gemora.unit.newsletter;

import com.gemora.GemoraApplication;
import com.gemora.newsletter.*;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static com.Gemora.unit.TestUtils.getBindingResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class NewsletterControllerTest {
    private NewsletterController emailController;

    @Mock
    private NewsletterService emailService;

    @BeforeEach
    void init() {
        emailController = new NewsletterController(emailService);
    }

    @Test
    void addEmail_ReturnsCreatedStatus_ValidEmail() {
        // given
        Newsletter validNewsletter = new Newsletter(1, "test@example.com");

        BindingResult bindingResult = getBindingResult(false);

        // when
        ResponseEntity<String> response = emailController.addEmail(validNewsletter, bindingResult);

        // then
        verify(emailService, times(1)).addEmail(validNewsletter);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Email added successfully.", response.getBody());
    }

    @Test
    void addEmail_ReturnsBadRequestStatus_InvalidEmail() {
        //given
        Newsletter invalidNewsletter = new Newsletter(1, "invalidEmail");

        BindingResult bindingResult = getBindingResult(true);

        //when
        ResponseEntity<String> response = emailController.addEmail(invalidNewsletter, bindingResult);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsBadRequestStatus_ThrowsEmailValidationException() {
        //given
        Newsletter newsletter = new Newsletter(1, "invalid@example.com");

        BindingResult bindingResult = getBindingResult(false);

        doThrow(EmailValidationException.class).when(emailService).addEmail(newsletter);

        //when
        ResponseEntity<String> response = emailController.addEmail(newsletter, bindingResult);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsConflictStatus_ThrowsEmailAlreadyExistsException() {
        //given
        Newsletter newsletter = new Newsletter(1, "existing@example.com");

        BindingResult bindingResult = getBindingResult(false);

        doThrow(EmailAlreadyExistsException.class).when(emailService).addEmail(newsletter);

        //when
        ResponseEntity<String> response = emailController.addEmail(newsletter, bindingResult);

        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void addEmail_ReturnsInternalServerErrorStatus_AnyOtherException() {
        //given
        Newsletter newsletter = new Newsletter(1, "any@example.com");

        BindingResult bindingResult = getBindingResult(false);

        doThrow(RuntimeException.class).when(emailService).addEmail(newsletter);

        //when
        ResponseEntity<String> response = emailController.addEmail(newsletter, bindingResult);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
