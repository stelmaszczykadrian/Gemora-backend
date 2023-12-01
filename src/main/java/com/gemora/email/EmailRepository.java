package com.gemora.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Integer> {
    Optional<Email> findByEmailAddress(String emailAddress);
}
