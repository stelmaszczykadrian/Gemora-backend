package com.gemora.email;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.gemora.validation.ValidationHelper.handleBindingResultErrors;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> addEmail(@Valid @RequestBody Email email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleBindingResultErrors(bindingResult);
        }

        try {
            emailService.addEmail(email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Email added successfully.");
        } catch (EmailValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add email. Please try again.");
        }
    }
}
