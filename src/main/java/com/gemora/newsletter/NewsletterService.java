package com.gemora.newsletter;

import com.gemora.validation.EmailValidator;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class NewsletterService {
    private final NewsletterRepository emailRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.emailRepository = newsletterRepository;
    }

    public void addEmail(Newsletter newsletter) {
        String emailAddress = newsletter.getEmailAddress();

        if (!EmailValidator.isValidEmail(emailAddress)) {
            throw new EmailValidationException("Invalid email format. Example: gemora@com.pl");
        }

        if (emailExists(emailAddress)) {
            throw new EmailAlreadyExistsException("Email already exists in the database.");
        }

        emailRepository.save(newsletter);
        log.info("Email added to database.");
    }

    private boolean emailExists(String emailAddress) {
        Optional<Newsletter> existingEmail = emailRepository.findByEmailAddress(emailAddress);

        return existingEmail.isPresent();
    }
}
