package com.Gemora.unit.newsletter;

import com.gemora.GemoraApplication;
import com.gemora.newsletter.*;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class NewsletterServiceTest {
    private NewsletterService emailService;

    private final String emailAddress = "test@example.com";

    @Mock
    private NewsletterRepository emailRepositoryMock;

    @BeforeEach
    void init() {
        emailService = new NewsletterService(emailRepositoryMock);
    }

    @Test
    void addEmail_ValidEmailAddress_SaveWasCalledOnce() {
        //given
        Newsletter newsletter = new Newsletter(1, emailAddress);

        //when
        emailService.addEmail(newsletter);

        //then
        verify(emailRepositoryMock, times(1)).save(any(Newsletter.class));
    }

    @Test
    void addEmail_ThrowsEmailValidationException_InvalidEmailAddress() {
        //given
        Newsletter newsletter = new Newsletter(1, "invalidEmail");

        //when & then
        assertThrows(EmailValidationException.class, () -> emailService.addEmail(newsletter));

        verify(emailRepositoryMock, never()).save(newsletter);
    }

    @Test
    void addEmail_ThrowsEmailAlreadyExistsException_DuplicateEmailAddress() {
        //given
        Newsletter newsletter = new Newsletter(1, emailAddress);
        when(emailRepositoryMock.findByEmailAddress(emailAddress)).thenReturn(Optional.of(newsletter));

        //when & then
        assertThrows(EmailAlreadyExistsException.class, () -> emailService.addEmail(newsletter));

        verify(emailRepositoryMock, never()).save(newsletter);
    }
}
