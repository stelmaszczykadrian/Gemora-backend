package com.gemora.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationHelper {
    public static ResponseEntity<String> handleBindingResultErrors(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = String.join("\n", errors);
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
