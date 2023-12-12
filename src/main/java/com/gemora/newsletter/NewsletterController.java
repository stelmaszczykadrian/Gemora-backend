package com.gemora.newsletter;

import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.gemora.validation.ValidationHelper.handleBindingResultErrors;

@RestController
@RequestMapping("/api/email")
@Slf4j
public class NewsletterController {
    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping
    public ResponseEntity<String> addEmail(@Valid @RequestBody Newsletter newsletter, BindingResult bindingResult) {
        log.info("Adding to newsletter");

        if (bindingResult.hasErrors()) {
            return handleBindingResultErrors(bindingResult);
        }

        try {
            newsletterService.addEmail(newsletter);
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
