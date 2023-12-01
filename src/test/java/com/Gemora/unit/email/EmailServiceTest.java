package com.Gemora.unit.email;

import com.gemora.GemoraApplication;
import com.gemora.email.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class EmailServiceTest {
    private EmailService emailService;

    private final String emailAddress = "test@example.com";

    @Mock
    private EmailRepository emailRepositoryMock;

    @BeforeEach
    void init() {
        emailService = new EmailService(emailRepositoryMock);
    }

    @Test
    void addEmail_ValidEmailAddress_SaveWasCalledOnce() {
        //given
        Email email = new Email(1, emailAddress);

        //when
        emailService.addEmail(email);

        //then
        verify(emailRepositoryMock, times(1)).save(any(Email.class));
    }

    @Test
    void addEmail_ThrowsEmailValidationException_InvalidEmailAddress() {
        //given
        Email email = new Email(1, "invalidEmail");

        //when & then
        assertThrows(EmailValidationException.class, () -> emailService.addEmail(email));

        verify(emailRepositoryMock, never()).save(email);
    }

    @Test
    void addEmail_ThrowsEmailAlreadyExistsException_DuplicateEmailAddress() {
        //given
        Email email = new Email(1, emailAddress);
        when(emailRepositoryMock.findByEmailAddress(emailAddress)).thenReturn(Optional.of(email));

        //when & then
        assertThrows(EmailAlreadyExistsException.class, () -> emailService.addEmail(email));

        verify(emailRepositoryMock, never()).save(email);
    }


}
