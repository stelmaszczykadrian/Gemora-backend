package com.gemora.email;

public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }
}
