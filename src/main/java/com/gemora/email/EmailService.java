package com.gemora.email;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailService {
    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public void addEmail(Email email) {
        String emailAddress = email.getEmailAddress();

        if (!EmailValidator.isValidEmail(emailAddress)) {
            throw new EmailValidationException("Invalid email format. Example: gemora@com.pl");
        }

        if (emailExists(emailAddress)) {
            throw new EmailAlreadyExistsException("Email already exists in the database.");
        }

        emailRepository.save(email);
    }

    private boolean emailExists(String emailAddress) {
        Optional<Email> existingEmail = emailRepository.findByEmailAddress(emailAddress);

        return existingEmail.isPresent();
    }
}
